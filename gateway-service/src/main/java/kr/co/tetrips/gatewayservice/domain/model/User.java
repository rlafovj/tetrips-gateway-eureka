package kr.co.tetrips.gatewayservice.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.tetrips.gatewayservice.domain.vo.Registration;
import kr.co.tetrips.gatewayservice.domain.vo.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
  private Long id;
  private String email;
  private String nickname;
  private List<Role> role;
  private Registration registration;
  private boolean gender;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate birthDate;
}
