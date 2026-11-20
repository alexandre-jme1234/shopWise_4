package com.shopwise.app.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record LigneVenteRequest(
		@NotNull(message = "Le produit est obligatoire")
		Long productId,

		@NotNull(message = "La quantité est obligatoire")
		@Positive(message = "La quantité doit être supérieure à 0")
		Integer quantiteProduit,

		@NotNull(message = "Le prix est obligatoire")
		@PositiveOrZero(message = "Le prix doit être positif ou nul")
		Integer prix) {
}
