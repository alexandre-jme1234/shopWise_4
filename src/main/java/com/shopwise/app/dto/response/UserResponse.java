package com.shopwise.app.dto.response;

// Pas de mot de passe dans la réponse, même haché
public record UserResponse(Long id, String username, String role) {
}
