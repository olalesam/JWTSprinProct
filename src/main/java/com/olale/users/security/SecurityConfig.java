package com.olale.users.security;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            AuthenticationManager authManager,
            SecretKey jwtSigningKey) throws Exception {

        var loginFilter = new JwtAuthenticationFilter(authManager, jwtSigningKey);
        var jwtFilter = new JwtAuthorizationFilter(authManager, jwtSigningKey);

        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(reg -> reg
                .requestMatchers("/auth/**", "/public/**").permitAll()
                .anyRequest().authenticated()
                )
                // Vérifie le JWT sur toutes les requêtes
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                // Gère le login /auth/login (JSON ou form)
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
                // pas de formLogin()/httpBasic() pour ne pas interférer
                .build();
    }
}
