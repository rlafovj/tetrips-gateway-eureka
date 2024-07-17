package kr.co.tetrips.gatewayservice.domain.dto;

import lombok.Data;

@Data
public class LoginDTO {
  private String email;
  private String password;
}
