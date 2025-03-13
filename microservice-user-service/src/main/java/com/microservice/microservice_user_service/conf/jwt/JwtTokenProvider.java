package com.microservice.microservice_user_service.conf.jwt;

import com.microservice.microservice_user_service.model.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Key JWT_SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS512); // Clé générée de taille appropriée
    private final long JWT_EXPIRATION = 604800000L; // 1 semaine

    public String generateToken(Authentication authentication, Set<Role> roles) {
        String username = authentication.getName();
        List<String> roleList = roles.stream()
                .map(role -> role.getName()) // Assurez-vous que `role.getName()` renvoie une chaîne comme "ROLE_USER"
                .collect(Collectors.toList());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roleList)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(JWT_SECRET)
                .compact();
    }


    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Retirer le "Bearer " du début du token
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public Authentication getAuthentication(String token) {
        String username = Jwts.parser().setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) Jwts.parser().setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody()
                .get("roles", List.class)
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(username, "", authorities);
    }

}
