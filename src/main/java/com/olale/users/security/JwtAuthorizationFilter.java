package com.olale.users.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final JWTVerifier verifier;

    // ➝ on injecte maintenant un JWTVerifier (fabriqué par une @Configuration)
    public JwtAuthorizationFilter(AuthenticationManager authManager, JWTVerifier verifier) {
        super(authManager);
        this.verifier = verifier;
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // Enlever le préfixe "Bearer "
            String jwt = auth.substring(7);

            // Vérifier et décoder le JWT
            DecodedJWT decoded = verifier.verify(jwt);

            // Récupérer subject (username) et rôles
            String username = decoded.getSubject();
            List<String> roles = decoded.getClaim("roles").asList(String.class);

            // Construire les autorités Spring
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            if (roles != null) {
                for (String r : roles) {
                    authorities.add(new SimpleGrantedAuthority(r));
                }
            }

            // Poser l'authentification dans le SecurityContext
            var authentication = new UsernamePasswordAuthenticationToken(
                    username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Continuer la chaîne
            chain.doFilter(request, response);

        } catch (Exception e) {
            // Token invalide/expiré → 401 et contexte nettoyé
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
