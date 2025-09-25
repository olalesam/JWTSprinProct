package com.olale.users.security;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWTVerifier;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            AuthenticationManager authManager,
            SecretKey jwtSigningKey, // toujours utile pour JwtAuthenticationFilter
            JWTVerifier jwtVerifier) // injecté depuis JwtKeyConfig
            throws Exception {

        var loginFilter = new JwtAuthenticationFilter(authManager, jwtSigningKey);
        var jwtFilter = new JwtAuthorizationFilter(authManager, jwtVerifier);

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reg -> reg
                .requestMatchers("/auth/**", "/public/**").permitAll()
                // accès réservé aux ADMIN
                .requestMatchers("/all").hasAuthority("ADMIN")
                .anyRequest().authenticated()
                )
                // Vérifie le JWT sur toutes les requêtes
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                // Gère le login /auth/login
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
