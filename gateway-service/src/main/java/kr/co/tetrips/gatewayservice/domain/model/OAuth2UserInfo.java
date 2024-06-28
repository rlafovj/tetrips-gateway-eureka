package kr.co.tetrips.gatewayservice.domain.model;

import kr.co.tetrips.gatewayservice.domain.vo.Registration;
import lombok.Builder;

import java.util.Map;

@Builder
public record OAuth2UserInfo(
        String id,
        String name,
        String email,
        String profile
) {

  public static OAuth2UserInfo of(Registration registrationId, Map<String, Object> attributes) {
    return switch (registrationId) {
      case GOOGLE -> ofGoogle(attributes);
      //case KAKAO -> ofKakao(attributes);
      //case NAVER -> ofNaver(attributes);
      default -> null;
    };
  }

  private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
    return OAuth2UserInfo.builder()
            .id((String) attributes.get("sub"))
            .name((String) attributes.get("name"))
            .email((String) attributes.get("email"))
            .profile((String) attributes.get("picture"))
            .build();
  }
}