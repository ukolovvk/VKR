package ru.ssau.auth.service;

import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author ukolov-victor
 */
@Service
public class JwtTokenService {

    @Value("${token.signing.key}")
    private String secretKey;

    @Value("${token.signing.ttl_ms}")
    private int ttl;

    public String createToken(String username) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.ttl);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

}
