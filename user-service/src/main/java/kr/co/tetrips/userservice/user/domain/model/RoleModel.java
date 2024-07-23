package kr.co.tetrips.userservice.user.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import kr.co.tetrips.userservice.user.domain.vo.Role;

@Entity(name = "ROLES")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class RoleModel {
  @Id
  @Column(name ="id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Role role;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserModel userModel;
}