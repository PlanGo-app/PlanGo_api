package com.plango.api.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtChecker extends OncePerRequestFilter{
	
	@Value("${jwt.secret}")
    private String secretKey;

	@Autowired
	UserDetailsService userDetailsService;

    private boolean check(String token) throws Exception {

    	try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			return true;
		} catch (SignatureException e) {
			throw new Exception("Invalid JWT signature");
		} catch (MalformedJwtException e) {
			throw new Exception("Invalid JWT token");
		} catch (ExpiredJwtException e) {
			throw new Exception("JWT token expired");
		} catch (IllegalArgumentException e) {
			throw new Exception("JWT is empty");
		}
    }

	@SneakyThrows
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String header = request.getHeader("Authorization");
    	String token = getToken(header);
    	try {
			if (token != null && check(token)) {
				String username = getUsernameWithValidToken(token);
				UserAuthDetails user = (UserAuthDetails) userDetailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
		filterChain.doFilter(request, response);
	}

	private String getToken(String header) {
		if (header != null && header.startsWith("Bearer ")) {
			return header.substring(7);
		}
		return null;
	}

	private String getUsernameWithValidToken(String token) {
		Claims claims = Jwts.parser()
				.setSigningKey(secretKey)
				.parseClaimsJws(token)
				.getBody();
		return claims.getSubject();
	}
}