package com.myfin.customer.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

	// Simple secret key (demo). Later secure place lo petkochu.
	private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	// 1 hour validity
	private final long jwtExpirationMs = 60 * 60 * 1000;

	public String generateToken(String email) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

		return Jwts.builder().setSubject(email).setIssuedAt(now).setExpiration(expiryDate).signWith(secretKey)
				.compact();
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public boolean isTokenValid(String token, String email) {
		String username = extractUsername(token);
		return username.equals(email) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		Date expiration = extractClaim(token, Claims::getExpiration);
		return expiration.before(new Date());
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();

		return claimsResolver.apply(claims);
	}
}
