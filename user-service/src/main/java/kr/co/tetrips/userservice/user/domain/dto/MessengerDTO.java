package kr.co.tetrips.userservice.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessengerDTO {
  private String message;
  private int status;
  private String accessToken;
  private String refreshToken;
  private Long id;
}