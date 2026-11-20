package com.shopwise.app.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.shopwise.app.entity.User;
import com.shopwise.app.repository.UserRepository;

// Crée un compte administrateur au démarrage s'il n'existe pas (dev uniquement).
// Le mot de passe vient de app.seed.admin-password, avec "admin" par défaut.
@Configuration
public class DataInitializer {

	@Bean
	public CommandLineRunner seedAdminUser(UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			@Value("${app.seed.admin-password:admin}") String adminPassword) {
		return args -> {
			if (userRepository.findByUsername("admin").isEmpty()) {
				userRepository.save(new User("admin", passwordEncoder.encode(adminPassword), "ADMIN"));
			}
		};
	}

}
