package com.shopwise.app;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.BeforeEach;

import com.shopwise.app.entity.User;
import com.shopwise.app.repository.UserRepository;

/**
 * Base des tests d'API : crée un CLIENT et un COMMERCANT en base et
 * fournit leur authentification Basic. Chaque test est rollbacké.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class AbstractApiTest {

	protected static final String PASSWORD = "motdepasse123";
	protected static final String USER_CLIENT = "client";
	protected static final String USER_COMMERCANT = "commercant";

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void creerUtilisateurs() {
		userRepository.save(new User(USER_CLIENT, passwordEncoder.encode(PASSWORD), "CLIENT"));
		userRepository.save(new User(USER_COMMERCANT, passwordEncoder.encode(PASSWORD), "COMMERCANT"));
	}

	protected RequestPostProcessor authClient() {
		return httpBasic(USER_CLIENT, PASSWORD);
	}

	protected RequestPostProcessor authCommercant() {
		return httpBasic(USER_COMMERCANT, PASSWORD);
	}

}
