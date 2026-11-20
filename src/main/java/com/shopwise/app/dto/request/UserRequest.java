package com.shopwise.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequest(
		@NotBlank(message = "Le nom d'utilisateur est obligatoire")
		@Size(min = 3, max = 50, message = "Le nom d'utilisateur doit faire entre 3 et 50 caractères")
		String username,

		@NotBlank(message = "Le mot de passe est obligatoire")
		@Size(min = 8, max = 100, message = "Le mot de passe doit faire entre 8 et 100 caractères")
		String password,

		// Optionnel : absent = USER
		@Pattern(regexp = "USER|ADMIN", message = "Le rôle doit être USER ou ADMIN")
		String role) {
}
