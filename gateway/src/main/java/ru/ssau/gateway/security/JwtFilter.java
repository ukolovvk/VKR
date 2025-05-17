package ru.ssau.gateway.security;

import java.net.URI;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author ukolov-victor
 */
@Component
public class JwtFilter implements GatewayFilter, Ordered {

    @Value("${token.signing.key}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("token");
        if (cookie == null) {
            exchange.getResponse().setStatusCode(HttpStatus.FOUND);
            exchange.getResponse().getHeaders().setLocation(URI.create("/login"));
            return exchange.getResponse().setComplete();
        }
        String token = cookie.getValue();

        if (StringUtils.isEmpty(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.FOUND);
            exchange.getResponse().getHeaders().setLocation(URI.create("/login"));
            return exchange.getResponse().setComplete();
        }

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
            String username = claims.getSubject();
            ResponseCookie usernameCookie = ResponseCookie.from("username", username)
                    .path("/")
                    .httpOnly(false)
                    .build();
            exchange.getResponse().addCookie(usernameCookie);
        } catch (Exception ex) {
            System.out.println("Invalid jwt token: " + ex.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.FOUND);
            exchange.getResponse().getHeaders().setLocation(URI.create("/login"));
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
