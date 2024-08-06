package kr.co.tetrips.gatewayservice.domain.vo;

import java.util.stream.Stream;

public enum Role {
  USER, ADMIN;

  public static Role getRole(String roleName) {
    return Stream.of(values()).filter(i -> i.name().equals(roleName)).findFirst().orElse(null);
  }
}
