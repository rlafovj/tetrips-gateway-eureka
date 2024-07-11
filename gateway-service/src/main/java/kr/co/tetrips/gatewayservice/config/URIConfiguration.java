package kr.co.tetrips.gatewayservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ConfigurationProperties
public class URIConfiguration {
  private String httpbin = "http://httpbin.org:80";
}
