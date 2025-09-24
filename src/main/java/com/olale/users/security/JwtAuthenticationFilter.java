package com.olale.users.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final SecretKey key;
    private final ObjectMapper mapper = new ObjectMapper();

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, SecretKey key) {
        setAuthenticationManager(authenticationManager);
        this.key = key;
        // Chemin de login (dans le "context-path" de l'appli)
        setFilterProcessesUrl("/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String ct = Optional.ofNullable(request.getContentType()).orElse("").toLowerCase();
        if (ct.contains("application/json")) {
            try {
                JsonNode node = mapper.readTree(request.getInputStream());
                String username = node.hasNonNull("username") ? node.get("username").asText() : "";
                String password = node.hasNonNull("password") ? node.get("password").asText() : "";
                var authRequest = new UsernamePasswordAuthenticationToken(username, password);
                setDetails(request, authRequest);
                return this.getAuthenticationManager().authenticate(authRequest);
            } catch (IOException e) {
                throw new AuthenticationServiceException("JSON de login invalide", e);
            }
        }
        // fallback: form-urlencoded (username/password)
        return super.attemptAuthentication(request, response);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
            org.springframework.security.core.Authentication auth)
            throws IOException, ServletException {

        String token = Jwts.builder()
                .setSubject(auth.getName())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        res.addHeader("Authorization", "Bearer " + token);
        res.setContentType("application/json");
        res.getWriter().write("{\"token\":\"" + token + "\"}");
        res.getWriter().flush();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            org.springframework.security.core.AuthenticationException failed)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Bad credentials\"}");
        response.getWriter().flush();
    }
}
