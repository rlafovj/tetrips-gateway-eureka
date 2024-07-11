package kr.co.tetrips.gatewayservice.service.impl;

import java.util.List;

import kr.co.tetrips.gatewayservice.domain.model.OAuth2UserInfo;
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

@Service
@RequiredArgsConstructor
public class PrincipalOauthUserService implements ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> {
  private final WebClient webClient;

  @Override
  public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    return null;
//
//    return new DefaultReactiveOAuth2UserService()
//            .loadUser(userRequest)
//            .log()
//            .flatMap(user -> Mono.just(user.getAttributes()))
//            .flatMap(attributes ->
//                    Mono.just(userRequest.getClientRegistration().getClientName())
//                            .log()
//                            .flatMap(clientId -> Mono.just(Registration.getRegistration(clientId)))
//                            .flatMap(registration ->
//                                    Mono.just(OAuth2UserInfo.of(registration, attributes))
//                                            .flatMap(oAuth2UserInfo ->
//                                                    Mono.just(
//                                                                    User.builder()
//                                                                            .id(oAuth2UserInfo.id())//개선필요
//                                                                            .email(oAuth2UserInfo.email())
//                                                                            .name(oAuth2UserInfo.name())
//                                                                            .profile(oAuth2UserInfo.profile())
//                                                                            .roles(List.of(Role.USER))
//                                                                            .registration(registration)
//                                                                            .build()
//                                                            )
//                                                            .filterWhen(i ->
//                                                                    webClient.post()
//                                                                            .uri("lb://user-service/auth/oauth2/" + i.getRegistration().name().toLowerCase())
//                                                                            .accept(MediaType.APPLICATION_JSON)
//                                                                            .bodyValue(i)
//                                                                            .retrieve()
//                                                                            .bodyToMono(Boolean.class)
//                                                            )
//                                                            .flatMap(user -> Mono.just(new PrincipalUserDetails(user, attributes)))//개선 필요
//                                            )
//                            )
//            );
  }
}
