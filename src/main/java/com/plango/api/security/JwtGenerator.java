package com.plango.api.security;

import java.util.Date;

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
        UserAuthDetails userAuthDetails = (UserAuthDetails) auth.getPrincipal();

        return Jwts.builder()
                    .setSubject(userAuthDetails.getUsername())
                    .setIssuedAt(new Date())
                    .signWith(SignatureAlgorithm.HS512, secretKey)
                    .compact();
    }
}