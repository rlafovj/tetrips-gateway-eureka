package kr.co.tetrips.gatewayservice.service.impl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
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
                                                .maxAge(jwtProvider.getAccessExpiredDate())
                                                .path("/")
                                                // .httpOnly(true)
                                                .build()
                                    )
                                    .cookie(
                                        ResponseCookie.from("refreshToken")
                                                .value(refreshToken)
                                                .maxAge(jwtProvider.getRefreshExpiredDate())
                                                .path("/")
                                                // .httpOnly(true)
                                                .build()
                                    )
                                    .cookie(
                                        ResponseCookie.from("username")
                                                .value(jwtProvider.extractEmail(accessToken))
                                                .maxAge(jwtProvider.getAccessExpiredDate())
                                                .path("/")
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
    return Mono.just(refreshToken)
        .flatMap(i -> Mono.just(jwtProvider.removeBearer(refreshToken)))
        .filter(i -> jwtProvider.isTokenValid(refreshToken, true))
        .filterWhen(i -> jwtProvider.isTokenInRedis(refreshToken))
        .flatMap(i -> Mono.just(jwtProvider.extractPrincipalUserDetails(refreshToken)))
        .flatMap(i -> jwtProvider.generateToken(i, false))
        .flatMap(accessToken ->
            ServerResponse.ok()
                .cookie(
                    ResponseCookie.from("accessToken")
                            .value(accessToken)
                            .maxAge(jwtProvider.getAccessExpiredDate())
                            .path("/")
                            // .httpOnly(true)
                            .build()
                )
                .build()
        );
  }

  @Override
  public Mono<ServerResponse> logout(String refreshToken) {
    log.info(">>> logout 진입");
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
      return ServerResponse.status(HttpStatus.OK)
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
}