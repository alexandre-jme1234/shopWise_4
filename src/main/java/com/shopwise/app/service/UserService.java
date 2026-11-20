package com.shopwise.app.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopwise.app.dto.request.SigninRequest;
import com.shopwise.app.entity.User;
import com.shopwise.app.exception.ForbiddenRoleException;
import com.shopwise.app.exception.UserAlreadyExistsException;
import com.shopwise.app.repository.UserRepository;

@Service
@Transactional
public class UserService {

	private static final String DEFAULT_ROLE = "USER";
	private static final String ADMIN_ROLE = "ADMIN";

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * @param requesterIsAdmin true si la requête est authentifiée par un administrateur
	 */
	public User signin(SigninRequest request, boolean requesterIsAdmin) {
		String role = request.getRole() == null ? DEFAULT_ROLE : request.getRole();

		// Empêche l'escalade de privilèges : seul un admin peut créer un admin
		if (ADMIN_ROLE.equals(role) && !requesterIsAdmin) {
			throw new ForbiddenRoleException();
		}

		if (userRepository.findByUsername(request.getUsername()).isPresent()) {
			throw new UserAlreadyExistsException(request.getUsername());
		}

		User user = new User(
				request.getUsername(),
				passwordEncoder.encode(request.getPassword()),
				role);

		try {
			return userRepository.save(user);
		} catch (DataIntegrityViolationException ex) {
			// Filet de sécurité : deux inscriptions simultanées du même nom
			// passent la vérification ci-dessus, mais l'index unique les refuse.
			throw new UserAlreadyExistsException(request.getUsername());
		}
	}

}
