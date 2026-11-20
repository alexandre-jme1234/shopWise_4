package com.shopwise.app.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopwise.app.dto.request.SigninRequest;
import com.shopwise.app.entity.User;
import com.shopwise.app.service.JwtService;
import com.shopwise.app.service.UserService;

import jakarta.validation.Valid;

// POST /api/auth/login : authentification Basic, puis renvoie un token Bearer (JWT).
// POST /api/auth/signin : inscription publique (route ouverte dans SecurityConfig).
// POST /api/auth/logout : géré par la configuration logout de SecurityConfig.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UserService userService;
	private final JwtService jwtService;

	public AuthController(UserService userService, JwtService jwtService) {
		this.userService = userService;
		this.jwtService = jwtService;
	}

	// Accepte Basic (identifiants) ou Bearer (token existant, pour le renouveler).
	// Dans les deux cas, Authentication contient le nom et les rôles de l'utilisateur.
	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(Authentication authentication) {
		// Seuls les rôles (ROLE_*) sont exposés, pas les facteurs FACTOR_*
		List<String> roles = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.filter(a -> a.startsWith("ROLE_"))
				.toList();

		String token = jwtService.generateToken(authentication.getName(), authentication.getAuthorities());

		return ResponseEntity.ok(Map.of(
				"accessToken", token,
				"tokenType", "Bearer",
				"expiresIn", jwtService.getExpirationSeconds(),
				"username", authentication.getName(),
				"roles", roles));
	}

	@PostMapping("/signin")
	public ResponseEntity<Map<String, Object>> signin(
			@Valid @RequestBody SigninRequest request,
			Authentication authentication) {

		// Fonctionne avec Basic comme avec Bearer. Anonyme : pas d'ADMIN.
		boolean requesterIsAdmin = authentication != null && authentication.isAuthenticated()
				&& authentication.getAuthorities().stream()
						.anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

		User created = userService.signin(request, requesterIsAdmin);

		// Le mot de passe (même haché) n'est jamais renvoyé
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
				"username", created.getUsername(),
				"role", created.getRole()));
	}

}
