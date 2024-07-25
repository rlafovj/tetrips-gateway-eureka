package kr.co.tetrips.gatewayservice.router;

import kr.co.tetrips.gatewayservice.config.URIConfiguration;
import kr.co.tetrips.gatewayservice.filter.AuthorizationHeaderFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Configuration
@RestController
@EnableConfigurationProperties(URIConfiguration.class)
public class GatewayRouter {
  @Bean
  public RouteLocator myRoutes(RouteLocatorBuilder builder, AuthorizationHeaderFilter authorizationHeaderFilter) {
    return builder.routes()
            .route(p -> p
                    .path("/get")
                    .filters(f -> f.addRequestHeader("Hello", "World"))
                    .uri("http://httpbin.org:80"))
            .route(p -> p
                    .host("*.circuitbreaker.com")
                    .filters(f -> f.circuitBreaker(config -> config
                            .setName("mycmd")
                            .setFallbackUri("forward:/fallback")))
                    .uri("http://httpbin.org:80"))
            .route(p -> p
                    .path("/user/getUserInfo")
                    .filters(f -> f.filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config())))
                    .uri("lb://USER/user/getUserInfo"))
            .route(p -> p
                    .path("/user/exists-email")
                    .filters(f -> f.filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config())))
                    .uri("lb://USER/user/exists-email"))
            .route(p -> p
                    .path("/user/exists-nickname")
                    .filters(f -> f.filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config())))
                    .uri("lb://USER/user/exists-nickname"))
            .build();

  }
  @RequestMapping("/fallback")
  public Mono<String> fallback(Throwable e) {
    return Mono.just("fallback Method from GatewayRouter , error is "+ e.getMessage());
  }
}