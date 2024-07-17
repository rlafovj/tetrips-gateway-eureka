package kr.co.tetrips.gatewayservice.service.impl;

import kr.co.tetrips.gatewayservice.domain.dto.OAuth2UserDTO;
import kr.co.tetrips.gatewayservice.domain.model.PrincipalUserDetails;
import kr.co.tetrips.gatewayservice.domain.model.User;
import kr.co.tetrips.gatewayservice.domain.vo.Registration;
import kr.co.tetrips.gatewayservice.domain.vo.Role;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrincipalOauthUserService implements ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> {
  private final WebClient webClient;

  @Override
  public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    return new DefaultReactiveOAuth2UserService()
            .loadUser(userRequest)
            .log()
            .flatMap(user -> Mono.just(user.getAttributes()))
            .flatMap(attributes ->
                    Mono.just(userRequest.getClientRegistration().getClientName())
                            .log()
                            .flatMap(clientId -> Mono.just(Registration.valueOf(clientId.toUpperCase())))
                            .flatMap(registration ->
                                    Mono.just(OAuth2UserDTO.of(registration, attributes))
                                            // .flatMap(oauth2UserDTO ->
                                            //     webClient.post()
                                            //     .uri("lb://user-service/auth/oauth2/" + registration.name().toLowerCase())
                                            //     .accept(MediaType.APPLICATION_JSON)
                                            //     .bodyValue(oauth2UserDTO)
                                            //     .retrieve()
                                            //     .bodyToMono(PrincipalUserDetails.class)
                                            // )
                                            .flatMap(oauth2UserDTO ->
                                                    Mono.just(new PrincipalUserDetails(
                                                                    User.builder()
                                                                            .email(oauth2UserDTO.email())
                                                                            .nickname(oauth2UserDTO.nickname())
                                                                            .role(List.of(Role.USER))
                                                                            .build(),
                                                                    attributes
                                                            )
                                                    )
                                            )
                            )
            )
            ;
  }
}
