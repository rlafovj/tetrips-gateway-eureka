package kr.co.tetrips.gatewayservice.service.provider;


import jakarta.annotation.PostConstruct;
import lombok.Getter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtProvider {
  private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

  private ReactiveValueOperations<String, String> reactiveValueOperations;

  private SecretKey SECRET_KEY;

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.iss}")
  private String issuer;

  @Getter
  @Value("${jwt.acc-exp}")
  private Long accessExpiredDate;

  @Getter
  @Value("${jwt.ref-exp}")
  private Long refreshExpiredDate;

  @PostConstruct
  protected void init() {
    SECRET_KEY = Keys.hmacShaKeyFor(Base64.getUrlEncoder().encode(secretKey.getBytes()));
    reactiveValueOperations = reactiveRedisTemplate.opsForValue();
  }

  public String extractEmail(String jwt){
    return extractClaim(jwt, Claims::getSubject);
  }

  @SuppressWarnings("unchecked")
  public List<String> extractRoles(String jwt){
    return extractClaim(jwt, i -> i.get("roles", List.class));
  }

  public Mono<String> generateToken(UserDetails userDetails, boolean isRefreshToken){
    return Mono.just(generateToken(Map.of(), userDetails, isRefreshToken))
        .flatMap(token ->
            isRefreshToken
                ? reactiveValueOperations.set(userDetails.getUsername(), token, Duration.ofSeconds(refreshExpiredDate)).flatMap(i -> Mono.just(token))
                : Mono.just(token)
        );
  }

  private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, boolean isRefreshToken) {
    String token = Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .issuer(issuer)
            .claim("roles", userDetails.getAuthorities().stream().map(i -> i.getAuthority()).toList())
            .claim("type", isRefreshToken ? "refresh" : "access")
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plus(isRefreshToken ? refreshExpiredDate : accessExpiredDate, ChronoUnit.MILLIS)))
            .signWith(SECRET_KEY, Jwts.SIG.HS256)
            .compact();
    log.info("발급된 토큰 : " + token);
    return token;
  }

//  public String createAccessToken(UserDTO user) {
//    String token = Jwts.builder()
//            .issuer(issuer)
//            .signWith(secretKey)
//            .expiration(Date.from(Instant.now().plus(accessExpiredDate, ChronoUnit.MILLIS)))
//            .subject("access")
//            .claim("userEmail", user.getEmail())
//            .claim("userId", user.getId())
//            .compact();
//    log.info("발급된 엑세스토큰 : " + token);
//    return token;
//  }
//  public String createRefreshToken(UserDTO user) {
//    String token = Jwts.builder()
//            .issuer(issuer)
//            .signWith(secretKey)
//            .expiration(Date.from(Instant.now().plus(refreshExpiredDate, ChronoUnit.MILLIS)))
//            .subject("refresh")
//            .claim("userEmail", user.getEmail())
//            .claim("userId", user.getId())
//            .compact();
//    log.info("발급된 리프레쉬토큰 : " + token);
//    return token;
//  }

  private <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver){
    return claimsResolver.apply(extractAllClaims(jwt));
  }

  private Claims extractAllClaims(String jwt){
    try {
      return Jwts.parser()
              .verifyWith(SECRET_KEY)
              .build()
              .parseSignedClaims(jwt)
              .getPayload();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }


  //For MVC
//  public String extractTokenFromHeader(HttpServletRequest request) {
//    String bearerToken = request.getHeader("Authorization");
//    if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
//      return bearerToken.substring(7);
//    }else {return "undefined token";}
//  }

  public void printPayload(String Token) {
    Base64.Decoder decoder = Base64.getDecoder();

    String[] chunk = Token.split("\\.");
    String payload = new String(decoder.decode(chunk[1]));
    String header = new String(decoder.decode(chunk[0]));

    log.info("Token Header : "+header);
    log.info("Token Payload : "+payload);

    //return payload;
  }

  public Claims getPayload(String token) {
    return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
  }

  public Long getRefreshExpired() {
    return Instant.now().plus(refreshExpiredDate, ChronoUnit.MILLIS).toEpochMilli();
  }

  public Boolean checkExpiration(String token){
    return Stream.of(Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token))
            .filter(i -> i.getPayload().getExpiration().after(Date.from(Instant.now())))
            .map(i -> true)
            .findAny()
            .orElseGet(() -> false);
  }

  public String updateExpiration(String token){
    return Stream.of(Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token))
            .map(i -> Jwts.builder()
                    .expiration(Date.from(Instant.now().plus(accessExpiredDate, ChronoUnit.MILLIS)))
                    .compact())
            .toString();

  }

  public String updateAccessToken(String oldToken){
    String newToken = Jwts.builder()
            .issuer(issuer)
            .signWith(SECRET_KEY)
            .expiration(Date.from(Instant.now().plus(accessExpiredDate, ChronoUnit.MILLIS)))
            .subject("access")
            .claim("userEmail", getPayload(oldToken).get("userEmail", String.class))
            .claim("userId", getPayload(oldToken).get("userId", Long.class))
            .compact();
    log.info("발급된 새 엑세스토큰 : " + newToken);
    return newToken;
  }


  public Boolean validateToken(String token, String subject) {
    try {
      return Stream.of(Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token))
              .filter(i -> i.getPayload().getSubject().equals(subject))
              .filter(i -> i.getPayload().getIssuer().equals(issuer))
              .map(i -> true)
              .findAny()
              .orElseGet(() -> false);
//    }catch (SignatureException e) {
//      log.info("Invalid JWT signature.");
//      log.trace("Invalid JWT signature trace: {}", e);
//    } catch (MalformedJwtException e) {
//      log.info("Invalid JWT token.");
//      log.trace("Invalid JWT token trace: {}", e);
//    } catch (ExpiredJwtException e) {
//      log.info("Expired JWT token.");
//      log.trace("Expired JWT token trace: {}", e);
//    } catch (UnsupportedJwtException e) {
//      log.info("Unsupported JWT token.");
//      log.trace("Unsupported JWT token trace: {}", e);
//    } catch (IllegalArgumentException e) {
//      log.info("JWT token compact of handler are invalid.");
//      log.trace("JWT token compact of handler are invalid trace: {}", e);
    } catch (Exception e) {
      return false;
    }
  }
}