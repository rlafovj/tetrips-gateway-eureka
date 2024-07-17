package kr.co.tetrips.gatewayservice.controller;

import kr.co.tetrips.gatewayservice.domain.dto.LoginDTO;
import kr.co.tetrips.gatewayservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

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
