package com.shopwise.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SigninRequest {

	@NotBlank(message = "Le nom d'utilisateur est obligatoire")
	@Size(min = 3, max = 50, message = "Le nom d'utilisateur doit faire entre 3 et 50 caractères")
	private String username;

	@NotBlank(message = "Le mot de passe est obligatoire")
	@Size(min = 8, max = 100, message = "Le mot de passe doit faire entre 8 et 100 caractères")
	private String password;

	// Optionnel : absent = USER. "ADMIN" n'est accepté que si la requête est faite par un administrateur.
	@Pattern(regexp = "USER|ADMIN", message = "Le rôle doit être USER ou ADMIN")
	private String role;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
