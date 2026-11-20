package com.shopwise.app.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

/**
 * Création d'une vente à partir d'un produit et d'un utilisateur (acheteur).
 * Le prix unitaire est celui du produit.
 */
public record CreerVenteRequest(
		@NotNull(message = "Le produit est obligatoire")
		Long productId,

		@NotNull(message = "L'utilisateur est obligatoire")
		Long userId,

		@NotNull(message = "La quantité est obligatoire")
		@Positive(message = "La quantité doit être supérieure à 0")
		Integer quantiteProduit,

		@NotNull(message = "La boutique est obligatoire")
		Long boutiqueId,

		@NotNull(message = "La date d'achat est obligatoire")
		@PastOrPresent(message = "La date d'achat ne peut pas être dans le futur")
		LocalDate dateAchat,

		// Optionnel : null = pas de réduction
		Long discountId) {
}
