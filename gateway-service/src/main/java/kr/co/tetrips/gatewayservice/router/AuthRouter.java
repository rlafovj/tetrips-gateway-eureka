package kr.co.tetrips.gatewayservice.router;

import kr.co.tetrips.gatewayservice.domain.dto.LoginDTO;
import kr.co.tetrips.gatewayservice.domain.dto.UserDTO;
import kr.co.tetrips.gatewayservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class AuthRouter {
  private final AuthService authService;

  //함수형 방식
  @Bean
  public RouterFunction<ServerResponse> authRoutes() {
    return RouterFunctions.route()
            .path("/auth", builder -> builder
                    .POST("/login/local", req -> req.bodyToMono(LoginDTO.class).flatMap(authService::localLogin))
                    .GET("/getNickname", req -> req.headers().header("Authorization").stream().findFirst().map(authService::getNickname).orElseGet(authService::createResponseForEmpty))
                    .POST("/refresh", req -> req.headers().header("Authorization").stream().findFirst().map(authService::refreshToken).orElseGet(authService::createResponseForEmpty))
                    .POST("/logout", req -> req.headers().header("Authorization").stream().findFirst().map(authService::logout).orElseGet(authService::createResponseForEmpty))
                    .GET("/getUserInfo", req -> req.headers().header("Authorization").stream().findFirst().map(authService::getUserInfo).orElseGet(authService::createResponseForEmpty))
            )
            .build();
  }

  //선언적 방식
//  @PostMapping("/login/local")
//  public Mono<ServerResponse> login(@RequestBody LoginDTO dto) {
//    return authService.localLogin(dto);
//  }
//
//  @PostMapping("/refresh")
//  public Mono<ServerResponse> refresh(@RequestHeader(name = "Authorization") String refreshToken) {
//    return authService.refreshToken(refreshToken).switchIfEmpty(authService.createResponseForEmpty());
//  }
//
//  @PostMapping("/logout")
//  public Mono<ServerResponse> logout(@RequestHeader(name = "Authorization") String refreshToken) {
//    return authService.logout(refreshToken);
//  }
}
