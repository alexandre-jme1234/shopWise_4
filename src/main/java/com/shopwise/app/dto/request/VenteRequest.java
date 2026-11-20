package com.shopwise.app.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

// total, quantité et prix ne sont pas saisis : ils sont calculés à partir des lignes.
public record VenteRequest(
		@NotNull(message = "La boutique est obligatoire")
		Long boutiqueId,

		// Optionnel : null = pas de réduction
		Long discountId,

		@NotNull(message = "La date d'achat est obligatoire")
		@PastOrPresent(message = "La date d'achat ne peut pas être dans le futur")
		LocalDate dateAchat,

		@NotEmpty(message = "Une vente doit contenir au moins une ligne")
		@Valid
		List<LigneVenteRequest> lignes) {
}
