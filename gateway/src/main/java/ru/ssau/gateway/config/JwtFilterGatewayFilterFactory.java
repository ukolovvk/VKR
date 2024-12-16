package ru.ssau.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import ru.ssau.gateway.security.JwtFilter;

/**
 * @author ukolov-victor
 */
@Component
public class JwtFilterGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {
    private final JwtFilter jwtFilter;

    public JwtFilterGatewayFilterFactory(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return jwtFilter;
    }
}