package kr.co.tetrips.gatewayservice.domain.vo;

import java.util.stream.Stream;

public enum Registration {
  LOCAL("local"), GOOGLE("google"), KAKAO("kakao"), NAVER("naver");

  public String name;

  Registration(String name) {
    this.name = name;
  }

  public static Registration getRegistration(String name) {
    return Stream.of(values()).filter(i -> i.name.equals(name)).findFirst().orElseThrow(() -> new IllegalArgumentException("Not supported registration type"));
  }
}
