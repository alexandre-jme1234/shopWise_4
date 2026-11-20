package com.shopwise.app.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	// La console H2 est une servlet hors DispatcherServlet : on utilise un matcher
	// basé sur le chemin (et non MVC) pour qu'elle soit bien reconnue.
	private static final PathPatternRequestMatcher H2_CONSOLE =
			PathPatternRequestMatcher.withDefaults().matcher("/h2-console/**");

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http,
			JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
		http
			.authorizeHttpRequests(auth -> auth
				// Console H2 (dev uniquement)
				.requestMatchers(H2_CONSOLE).permitAll()
				// Inscription publique : pas d'identifiants requis
				.requestMatchers(HttpMethod.POST, "/api/auth/signin").permitAll()
				// Tout le reste, y compris /api/auth/login et /api/auth/logout, exige Basic Auth
				// Catalogue produits : réservé au commerçant (et à l'admin)
				.requestMatchers("/api/products", "/api/products/**").hasAnyRole("COMMERCANT", "ADMIN")
				// Modification des lignes d'une vente : réservé au commerçant (et à l'admin)
				.requestMatchers(HttpMethod.DELETE, "/api/ventes/*/lignes/*").hasAnyRole("COMMERCANT", "ADMIN")
				.requestMatchers(HttpMethod.PUT, "/api/ventes/*/lignes/*").hasAnyRole("COMMERCANT", "ADMIN")
				.anyRequest().authenticated()
			)
			// Authentification HTTP Basic : identifiants envoyés à chaque requête
			.httpBasic(Customizer.withDefaults())
			// Bearer : token JWT envoyé dans "Authorization: Bearer <token>"
			.oauth2ResourceServer(oauth -> oauth
				.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
			// Pas de session serveur : chaque requête est authentifiée par son en-tête Basic
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// Logout : vide le contexte de sécurité et répond 204.
			// Avec Basic Auth, le serveur ne peut pas révoquer les identifiants :
			// le client doit oublier ses identifiants après cet appel.
			.logout(logout -> logout
				.logoutUrl("/api/auth/logout")
				.logoutSuccessHandler((request, response, authentication) ->
						response.setStatus(HttpStatus.NO_CONTENT.value()))
			)
			// La console H2 utilise des frames et des POST sans token CSRF
			.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
			// API stateless en Basic/Bearer : pas de CSRF sur les routes d'API.
			// Sans cette exclusion, le POST est rejeté avant l'authentification (401 au lieu de 201).
			.csrf(csrf -> csrf.ignoringRequestMatchers(
					H2_CONSOLE,
					PathPatternRequestMatcher.withDefaults().matcher("/api/**"),
					PathPatternRequestMatcher.withDefaults().matcher("/creer-vente"),
					PathPatternRequestMatcher.withDefaults().matcher("/modifier-vente")));

		return http.build();
	}

}
