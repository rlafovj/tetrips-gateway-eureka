package kr.co.tetrips.gatewayservice.filter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class QueryParamGatewayFilterFactory extends AbstractGatewayFilterFactory<QueryParamGatewayFilterFactory.Config> {

  public QueryParamGatewayFilterFactory() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      String email = exchange.getRequest().getQueryParams().getFirst("email");
      String nickname = exchange.getRequest().getQueryParams().getFirst("nickname");
      if (email != null) {
        exchange.getAttributes().put("email", email);
      }
      if (nickname != null) {
        exchange.getAttributes().put("nickname", URLEncoder.encode(nickname, StandardCharsets.UTF_8));
      }
      return chain.filter(exchange);
    };
  }

  public static class Config {
    // Put configuration properties if required
  }
}