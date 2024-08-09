package kr.co.tetrips.gatewayservice.service.impl;
import kr.co.tetrips.gatewayservice.domain.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import kr.co.tetrips.gatewayservice.domain.dto.LoginDTO;
import kr.co.tetrips.gatewayservice.domain.model.PrincipalUserDetails;
import kr.co.tetrips.gatewayservice.service.AuthService;
import kr.co.tetrips.gatewayservice.service.provider.JwtProvider;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
  private final WebClient webClient;
  private final JwtProvider jwtProvider;

  @Override
  public Mono<ServerResponse> localLogin(LoginDTO dto) {
    return Mono.just(dto)
        .log()
        .flatMap(i ->
            webClient.post()
                .uri("lb://USER/auth/login/local")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(i)
                .retrieve()
                .bodyToMono(PrincipalUserDetails.class)
            )
            .filter(i -> !Objects.equals(i.getUsername(), "Login Fail")) // email 값이 Login Fail 이 아닌 경우에만 토큰 생성 진행
            .flatMap(i ->
                jwtProvider.generateToken(i, false)
                    .flatMap(accessToken ->
                        jwtProvider.generateToken(i, true)
                            .flatMap(refreshToken ->
                                ServerResponse.ok()
                                    .cookie(
                                        ResponseCookie.from("accessToken")
                                                .value(accessToken)
                                                .maxAge(jwtProvider.getAccessExpired()/1000)
                                                .path("/")
//                                                .secure(false) // for test
//                                                .sameSite("None") // for test
                                                // .httpOnly(true)
                                                .build()
                                    )
                                    .cookie(
                                        ResponseCookie.from("refreshToken")
                                                .value(refreshToken)
                                                .maxAge(jwtProvider.getRefreshExpired()/1000)
                                                .path("/")
//                                                .secure(false) // for test
//                                                .sameSite("None") // for test
                                                // .httpOnly(true)
                                                .build()
                                    )
                                    .cookie(
                                        ResponseCookie.from("username")
                                                .value(jwtProvider.extractEmail(accessToken))
                                                .maxAge(jwtProvider.getRefreshExpired()/1000)
                                                .path("/")
//                                                .secure(false) // for test
//                                                .sameSite("None") // for test
                                                // .httpOnly(true
                                                .build()
                                    )
//                                        .cookie(
//                                        ResponseCookie.from("nickname")
//                                                .value(jwtProvider.extractNickname(accessToken))
//                                                .maxAge(jwtProvider.getAccessExpiredDate())
//                                                .path("/")
//                                                // .httpOnly(true
//                                                .build()
//                                    )
                                    .build()
                            )
                    )
            )
            .switchIfEmpty(Mono.defer(() -> {
              String message = "Login Failed: email or password is incorrect";
              return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
//                      .contentType(MediaType.APPLICATION_JSON)
//                      .body(BodyInserters.fromValue(Collections.singletonMap("message", message)));
            }));
  }

  @Override
  public Mono<ServerResponse> refreshToken(String refreshToken) {
    log.info("refreshToken 기반 accessToken 재발급 요청");
    return Mono.just(refreshToken)
        .flatMap(i -> Mono.just(jwtProvider.removeBearer(refreshToken)))
        .doOnNext(i -> log.info("After removeBearer: " + i))
        .filter(i -> jwtProvider.isTokenValid(refreshToken, true))
        .doOnNext(i -> log.info("After isTokenValid: " + i))
//        .filterWhen(i -> jwtProvider.isTokenInRedis(refreshToken))
//        .doOnNext(i -> log.info("After isTokenInRedis: " + i))
        .flatMap(i -> Mono.just(jwtProvider.extractPrincipalUserDetails(refreshToken)))
        .doOnNext(i -> log.info("After extractPrincipalUserDetails: " + i))
        .flatMap(i -> jwtProvider.generateToken(i, false))
        .doOnNext(i -> log.info("After generateToken: " + i))
        .flatMap(accessToken ->
            ServerResponse.ok()
                .cookie(
                    ResponseCookie.from("accessToken")
                            .value(accessToken)
                            .maxAge(jwtProvider.getAccessExpiredDate())
                            .path("/")
//                            .secure(false) // for test
//                            .sameSite("None") // for test
                            // .httpOnly(true)
                            .build()
                )
                .build()
        ).switchIfEmpty(Mono.defer(() -> {
              String message = "@@@@@@@@Refresh Failed: refreshToken is invalid or expired@@@@@@@@@@@@@@";
              return ServerResponse.status(HttpStatus.UNAUTHORIZED)
//                      .contentType(MediaType.APPLICATION_JSON)
                      .body(BodyInserters.fromValue(Collections.singletonMap("message", message)));
            }));
  }

  @Override
  public Mono<ServerResponse> logout(String refreshToken) {
    return Mono.just(refreshToken)
            .flatMap(i -> Mono.just(jwtProvider.removeBearer(refreshToken)))
            .filter(i -> jwtProvider.isTokenValid(refreshToken, true))
            .filterWhen(i -> jwtProvider.isTokenInRedis(refreshToken))
            .filterWhen(i -> jwtProvider.removeTokenInRedis(refreshToken))
            .flatMap(i -> ServerResponse.ok().build());
  }

  @Override
  public Mono<ServerResponse> createResponseForEmpty() {
    return Mono.defer(() -> {
      String message = "Please Login Again";
      return ServerResponse.status(HttpStatus.UNAUTHORIZED)
              .contentType(MediaType.APPLICATION_JSON)
              .body(BodyInserters.fromValue(Collections.singletonMap("message", message)));
    });
  }

  @Override
  public Mono<ServerResponse> getNickname(String accessToken) {
    return Mono.just(accessToken)
            .flatMap(i -> Mono.just(jwtProvider.removeBearer(accessToken)))
            .filter(i -> jwtProvider.isTokenValid(accessToken, false))
            .flatMap(i -> Mono.just(jwtProvider.extractEmail(i)))
            .flatMap(i -> webClient.post()
                    .uri("lb://USER/auth/getNickname")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(Collections.singletonMap("email", i))
                    .retrieve()
                    .bodyToMono(String.class)
            ).flatMap(i -> ServerResponse.ok().body(BodyInserters.fromValue(Collections.singletonMap("nickname", i))));
  }

  @Override
  public Mono<ServerResponse> getUserInfo(String accessToken) {
    return Mono.just(accessToken)
            .flatMap(i -> Mono.just(jwtProvider.removeBearer(accessToken)))
            .filter(i -> jwtProvider.isTokenValid(accessToken, false))
            .flatMap(i -> Mono.just(jwtProvider.extractEmail(i)))
            .flatMap(i -> webClient.post()
                    .uri("lb://USER/user/getUserInfo")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(Collections.singletonMap("email", i))
                    .retrieve()
                    .bodyToMono(UserDTO.class)
            ).flatMap(i -> ServerResponse.ok().body(BodyInserters.fromValue(i)));
  }
}