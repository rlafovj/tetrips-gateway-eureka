package kr.co.tetrips.userservice.user.domain.model;

import jakarta.persistence.*;
import kr.co.tetrips.userservice.user.domain.vo.Registration;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Entity(name = "USERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@ToString(exclude = {"id"})
public class UserModel {
  @Id
  @Column(name = "ID", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String email;

  @Column(name = "PASSWORD")
  private String password;

  @Column(name = "NICKNAME")
  private String nickname;

  @Column(name = "GENDER", nullable = true)
  private boolean gender;

  @Column(name = "BIRTH_DATE", nullable = true)
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate birthDate;

  @Column(name = "REGISTRATION")
  private Registration registration;

  @Setter
  @OneToMany(mappedBy = "userId", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
  private List<RoleModel> roleId;

}
