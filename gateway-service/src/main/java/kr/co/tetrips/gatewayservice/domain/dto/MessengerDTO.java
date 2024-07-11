package kr.co.tetrips.gatewayservice.domain.dto;

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
  private Long id;
  private String message;
  private int status;
  private Object data;
  private String accessToken;
  private String refreshToken;
}