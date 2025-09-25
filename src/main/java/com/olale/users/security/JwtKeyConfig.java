package com.olale.users.security;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

@Configuration
public class JwtKeyConfig {

    @Value("${security.jwt.secret}")
    private String secret; // vient de application.properties

    // Bean utilisé par JwtAuthenticationFilter (émission)
    @Bean
    public SecretKey jwtSigningKey() {
        byte[] decoded = Base64.getDecoder().decode(secret);
        return new SecretKeySpec(decoded, "HmacSHA256");
    }

    // Bean Algorithm pour Auth0
    @Bean
    public Algorithm jwtAlgorithm() {
        byte[] decoded = Base64.getDecoder().decode(secret);
        return Algorithm.HMAC256(decoded);
    }

    // Bean JWTVerifier pour JwtAuthorizationFilter (validation)
    @Bean
    public JWTVerifier jwtVerifier(Algorithm algorithm) {
        return JWT.require(algorithm).build();
    }
}
