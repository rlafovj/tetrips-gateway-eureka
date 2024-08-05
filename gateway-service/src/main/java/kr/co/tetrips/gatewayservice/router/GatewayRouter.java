package kr.co.tetrips.gatewayservice.router;

import kr.co.tetrips.gatewayservice.config.URIConfiguration;
import kr.co.tetrips.gatewayservice.filter.AuthorizationHeaderFilter;
import kr.co.tetrips.gatewayservice.filter.QueryParamGatewayFilterFactory;
import kr.co.tetrips.gatewayservice.filter.QueryParamRewriteFilter;
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
  public RouteLocator myRoutes(RouteLocatorBuilder builder,
                               AuthorizationHeaderFilter authorizationHeaderFilter,
                               QueryParamGatewayFilterFactory queryParamFilter,
                               QueryParamRewriteFilter queryParamRewriteFilter) {
    return builder.routes()
            .route(p -> p
                    .path("/auth/signup")
                    .uri("lb://USER/auth/signup"))
            .route(p -> p
                    .path("/user/getUserInfo")
                    .filters(f -> f.filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config()))
                            .filter(queryParamFilter.apply(new QueryParamGatewayFilterFactory.Config()))
                            .filter(queryParamRewriteFilter))
                    .uri("lb://USER/user/getUserInfo"))
            .route(p -> p
                    .path("/user/exists-email")
                    .filters(f -> f
                            .filter(queryParamFilter.apply(new QueryParamGatewayFilterFactory.Config()))
                            .filter(queryParamRewriteFilter))
                    .uri("lb://USER/user/exists-email"))
            .route(p -> p
                    .path("/user/exists-nickname")
                    .filters(f -> f
                            .filter(queryParamFilter.apply(new QueryParamGatewayFilterFactory.Config()))
                            .filter(queryParamRewriteFilter))
                    .uri("lb://USER/user/exists-nickname"))
            .route(p -> p
                    .path("/user/updateUserInfo")
                    .filters(f -> f.filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config())))
                    .uri("lb://USER/user/updateUserInfo"))
            .route(p -> p
                    .path("/user/deleteUser")
                    .filters(f -> f.filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config()))
                            .filter(queryParamFilter.apply(new QueryParamGatewayFilterFactory.Config()))
                            .filter(queryParamRewriteFilter))
                    .uri("lb://USER/user/deleteUser"))
//            .route(p -> p
//                    .path("/swagger/**")
//                    .uri("lb://USER")
//            ) //for swagger
            .build();

  }
}