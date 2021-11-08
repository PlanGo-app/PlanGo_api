package com.plango.api.security;

import java.util.Date;

import com.plango.api.entity.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtGenerator {
    
    @Value("${jwt.secret}")
    private String secretKey;

    public String generateToken(Authentication auth) {
        User user = (User) auth.getPrincipal();

        return Jwts.builder()
                    .setSubject(user.getPseudo())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 850000))
                    .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
                    .compact();
    }
}