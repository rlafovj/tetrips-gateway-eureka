package kr.co.tetrips.userservice.user.domain.vo;

import lombok.AllArgsConstructor;
import java.util.stream.Stream;

@AllArgsConstructor
public enum Role {
  USER(0), ADMIN(1);

  private int roleCode;

  public static Role getRole(int roleCode) {
    return Stream.of(values()).filter(i -> i.roleCode == roleCode).findFirst().orElse(null);
  }
}