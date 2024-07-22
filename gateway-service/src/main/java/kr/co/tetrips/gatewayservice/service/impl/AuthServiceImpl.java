package kr.co.tetrips.gatewayservice.service.impl;
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

@Service
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
                .uri("lb://user-service/auth/login/local")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(i)
                .retrieve()
                .bodyToMono(PrincipalUserDetails.class)
            )
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
                                    ).cookie(
                                        ResponseCookie.from("nickname")
                                                .value(jwtProvider.extractNickname(accessToken))
                                                .maxAge(jwtProvider.getAccessExpiredDate())
                                                .path("/")
                                                // .httpOnly(true
                                                .build()
                                        )
                                    .build()
                            )
                    )
            );
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
}