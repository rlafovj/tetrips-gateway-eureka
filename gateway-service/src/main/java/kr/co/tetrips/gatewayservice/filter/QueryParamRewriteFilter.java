package kr.co.tetrips.gatewayservice.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class QueryParamRewriteFilter implements GatewayFilter {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String email = exchange.getAttribute("email");
    String nickname = exchange.getAttribute("nickname");
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(exchange.getRequest().getURI());
    if (email != null) {
      uriBuilder.replaceQueryParam("email", email);
    }
    if (nickname != null) {
      uriBuilder.replaceQueryParam("nickname", nickname);
    }
    URI newUri = uriBuilder.build(true).toUri();
    exchange = exchange.mutate().request(exchange.getRequest().mutate().uri(newUri).build()).build();
    return chain.filter(exchange);
  }
}