package kr.co.tetrips.gatewayservice.domain.dto;

import kr.co.tetrips.gatewayservice.domain.vo.Registration;
import lombok.Builder;

import java.util.Map;

@Builder
public record OAuth2UserDTO(
        String id,
        String name,
        String email,
        String profile
) {

  public static OAuth2UserDTO of(Registration registrationId, Map<String, Object> attributes) {
    return switch (registrationId) {
      case GOOGLE -> ofGoogle(attributes);
      //case KAKAO -> ofKakao(attributes);
      //case NAVER -> ofNaver(attributes);
      default -> null;
    };
  }

  private static OAuth2UserDTO ofGoogle(Map<String, Object> attributes) {
    return OAuth2UserDTO.builder()
            .id((String) attributes.get("sub"))
            .name((String) attributes.get("name"))
            .email((String) attributes.get("email"))
            .profile((String) attributes.get("picture"))
            .build();
  }
}