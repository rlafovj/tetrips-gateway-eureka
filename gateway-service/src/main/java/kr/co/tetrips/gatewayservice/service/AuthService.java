package kr.co.tetrips.gatewayservice.service;

import kr.co.tetrips.gatewayservice.domain.dto.LoginDTO;
import kr.co.tetrips.gatewayservice.domain.dto.UserDTO;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface AuthService {
  Mono<ServerResponse> localLogin(LoginDTO dto);
  Mono<ServerResponse> logout(String refreshToken);
  Mono<ServerResponse> refreshToken(String refreshToken);
  Mono<ServerResponse> createResponseForEmpty();

  Mono<ServerResponse> getNickname(String accessToken);

}
