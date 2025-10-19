package com.transi.flex.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

	public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
	private static final long EXPIRATION = 1000L * 60 * 60 * 24 * 7;

	public String generateToken(String userName, Long companyId, Long agencyId) {
		Map<String, Object> claims = new HashMap<>();
		if (agencyId != null) {
			claims.put("agencyId", agencyId);
		}
		return createToken(claims, userName, companyId);
	}

	public String generateTmpToken(String userName) {
		Map<String, Object> claims = new HashMap<>();
		return Jwts.builder().setClaims(claims).setSubject(userName).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}

	private String createToken(Map<String, Object> claims, String userName, Long companyId) {
		return Jwts.builder().setClaims(claims).setSubject(userName)
				.setAudience(companyId == null ? null : companyId.toString())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}

	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	public Long getAudienceFromJwtToken(String token) {
		var companyId = Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody()
				.getAudience();
		return companyId == null ? null : Long.valueOf(companyId);
	}

	public Long getAgencyIdFromJwtToken(String token) {
		Claims claims = extractAllClaims(token);
		Object agencyId = claims.get("agencyId");
		return agencyId == null ? null : Long.valueOf(agencyId.toString());
	}
}