package com.olale.users.security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class JwtKeyConfig {

    @Bean
    public SecretKey jwtSigningKey(@Value("${security.jwt.secret}") String base64Secret) {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        if (keyBytes.length < 64) { // HS512 => >= 64 octets
            throw new IllegalStateException("security.jwt.secret doit décoder à >= 64 octets pour HS512");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
