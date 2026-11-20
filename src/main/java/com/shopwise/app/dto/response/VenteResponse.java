package com.shopwise.app.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.shopwise.app.entity.Vente;

public record VenteResponse(
		Long id,
		Long boutiqueId,
		Long discountId,
		Long userId,
		LocalDate dateAchat,
		Integer quantiteTotale,
		Integer total,
		Float prix,
		List<LigneVenteResponse> lignes) {

	public static VenteResponse from(Vente vente) {
		return new VenteResponse(
				vente.getId(),
				vente.getBoutiqueId(),
				vente.getDiscount() == null ? null : vente.getDiscount().getId(),
				vente.getUser() == null ? null : vente.getUser().getId(),
				vente.getDateAchat(),
				vente.getQuantiteTotale(),
				vente.getTotal(),
				vente.getPrix(),
				vente.getLignes().stream().map(LigneVenteResponse::from).toList());
	}
}
