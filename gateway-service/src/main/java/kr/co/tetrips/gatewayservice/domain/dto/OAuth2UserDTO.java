package kr.co.tetrips.gatewayservice.domain.dto;

import kr.co.tetrips.gatewayservice.domain.vo.Registration;
import lombok.Builder;

import java.util.Map;

@Builder
public record OAuth2UserDTO(
        String id,
        String nickname,
        String email,
        String profile
) {

  public static OAuth2UserDTO of(Registration registration, Map<String, Object> attributes) {
    return switch (registration) {
      case GOOGLE -> ofGoogle(attributes);
      //case KAKAO -> ofKakao(attributes);
      case NAVER -> ofNaver(attributes);
      default -> null;
    };
  }

  private static OAuth2UserDTO ofGoogle(Map<String, Object> attributes) {
    return OAuth2UserDTO.builder()
            .id((String) attributes.get("sub"))
            .nickname((String) attributes.get("name"))
            .email((String) attributes.get("email"))
            .profile((String) attributes.get("picture"))
            .build();
  }

  private static OAuth2UserDTO ofNaver(Map<String, Object> attributes) {
    Map<String, Object> response = (Map<String, Object>) attributes.get("response");
    return OAuth2UserDTO.builder()
            .id((String) response.get("id"))
            .nickname((String) response.get("nickname"))
            .email((String) response.get("email"))
            .build();
  }
}