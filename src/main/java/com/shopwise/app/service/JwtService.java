package com.shopwise.app.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	private static final String ISSUER = "shopwise";

	private final JwtEncoder jwtEncoder;
	private final long expirationMinutes;

	public JwtService(JwtEncoder jwtEncoder,
			@Value("${app.jwt.expiration-minutes:60}") long expirationMinutes) {
		this.jwtEncoder = jwtEncoder;
		this.expirationMinutes = expirationMinutes;
	}

	public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
		Instant now = Instant.now();

		// Seuls les rôles sont retenus : Spring ajoute aussi des "facteurs"
		// (FACTOR_PASSWORD, FACTOR_BEARER) qui ne sont pas des rôles.
		// Rôles sans préfixe : "ROLE_ADMIN" devient "ADMIN" dans le claim
		List<String> roles = authorities.stream()
				.map(GrantedAuthority::getAuthority)
				.filter(a -> a.startsWith("ROLE_"))
				.map(a -> a.substring("ROLE_".length()))
				.toList();

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(ISSUER)
				.subject(username)
				.issuedAt(now)
				.expiresAt(now.plus(expirationMinutes, ChronoUnit.MINUTES))
				.claim("roles", roles)
				.build();

		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}

	public long getExpirationSeconds() {
		return expirationMinutes * 60;
	}

}
