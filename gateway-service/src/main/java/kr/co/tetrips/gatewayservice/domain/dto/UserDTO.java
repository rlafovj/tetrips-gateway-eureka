package kr.co.tetrips.gatewayservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.tetrips.gatewayservice.domain.vo.Registration;
import kr.co.tetrips.gatewayservice.domain.vo.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
@Component
@NoArgsConstructor
@Data
public class UserDTO {
  private Long id;
  private String email;
  private String password;
  private String nickname;
  private boolean gender;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate birthDate;
  private Registration registration;
  private List<Role> role;
}
