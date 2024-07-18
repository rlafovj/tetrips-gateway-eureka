package kr.co.tetrips.gatewayservice.router;

import kr.co.tetrips.gatewayservice.domain.dto.LoginDTO;
import kr.co.tetrips.gatewayservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthRouter {
  private final AuthService authService;
  //private final AuthFilter authFilter; //= authService

  @Bean
  RouterFunction<ServerResponse> authRoutes() {
    return RouterFunctions.route()
            .POST("/auth/login/local", req -> req.bodyToMono(LoginDTO.class).flatMap(authFilter::localLogin))
            .build();
  }

  @PostMapping("/login/local")
  public Mono<ServerResponse> login(@RequestBody LoginDTO dto) {
    return authService.localLogin(dto);
  }

  @PostMapping("/refresh")
  public Mono<ServerResponse> refresh(@RequestHeader(name = "Authorization") String refreshToken) {
    return authService.refreshToken(refreshToken).switchIfEmpty(authService.createResponseForEmpty());
  }

  @PostMapping("/logout")
  public Mono<ServerResponse> logout(@RequestHeader(name = "Authorization") String refreshToken) {
    return authService.logout(refreshToken);
  }
}
