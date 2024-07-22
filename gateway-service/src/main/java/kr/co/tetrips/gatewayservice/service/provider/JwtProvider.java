package kr.co.tetrips.gatewayservice.service.provider;


import jakarta.annotation.PostConstruct;
import kr.co.tetrips.gatewayservice.domain.model.PrincipalUserDetails;
import kr.co.tetrips.gatewayservice.domain.model.User;
import kr.co.tetrips.gatewayservice.domain.vo.Role;
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

  public String extractNickname(String jwt){
    return extractClaim(jwt, i -> i.get("nickname", String.class));
  }

  @SuppressWarnings("unchecked")
  public List<String> extractRoles(String jwt){
    return extractClaim(jwt, i -> i.get("role", List.class));
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
            .claim("role", userDetails.getAuthorities().stream().map(i -> i.getAuthority()).toList())
            .claim("type", isRefreshToken ? "refresh" : "access")
            .claim("nickname", ((PrincipalUserDetails) userDetails).getUser().getNickname())
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

  public String removeBearer(String bearerToken){
    return bearerToken.replace("Bearer ", "");
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

//  public Long getRefreshExpired() {
//    return Instant.now().plus(refreshExpiredDate, ChronoUnit.MILLIS).toEpochMilli();
//  }
//
//  public Boolean checkExpiration(String token){
//    return Stream.of(Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token))
//            .filter(i -> i.getPayload().getExpiration().after(Date.from(Instant.now())))
//            .map(i -> true)
//            .findAny()
//            .orElseGet(() -> false);
//  }
//
//  public String updateExpiration(String token){
//    return Stream.of(Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token))
//            .map(i -> Jwts.builder()
//                    .expiration(Date.from(Instant.now().plus(accessExpiredDate, ChronoUnit.MILLIS)))
//                    .compact())
//            .toString();
//
//  }
//
//  public String updateAccessToken(String oldToken){
//    String newToken = Jwts.builder()
//            .issuer(issuer)
//            .signWith(SECRET_KEY)
//            .expiration(Date.from(Instant.now().plus(accessExpiredDate, ChronoUnit.MILLIS)))
//            .subject("access")
//            .claim("userEmail", getPayload(oldToken).get("userEmail", String.class))
//            .claim("userId", getPayload(oldToken).get("userId", Long.class))
//            .compact();
//    log.info("발급된 새 엑세스토큰 : " + newToken);
//    return newToken;
//  }


  public Boolean isTokenValid(String token, Boolean isRefreshToken) {
    return !isTokenExpired(token) && isTokenTypeEqual(token, isRefreshToken);
  }

  public Mono<Boolean> isTokenInRedis(String token){
    return reactiveValueOperations.get(token)
            .flatMap(i -> Mono.just(i != null));
  }

  private Boolean isTokenExpired(String token){
    return extractClaim(token, Claims::getExpiration).before(Date.from(Instant.now()));
  }

  private Boolean isTokenTypeEqual(String token, Boolean isRefreshToken){
    return extractClaim(token, i -> i.get("type", String.class)).equals(isRefreshToken ? "refresh" : "access");
  }

  public PrincipalUserDetails extractPrincipalUserDetails(String jwt){
    return new PrincipalUserDetails(User.builder().email(extractEmail(jwt)).role(extractRoles(jwt).stream().map(i -> Role.valueOf(i)).toList()).build());
  }

  public Mono<Boolean> removeTokenInRedis(String token){
    return reactiveValueOperations.delete(token);
  }
}