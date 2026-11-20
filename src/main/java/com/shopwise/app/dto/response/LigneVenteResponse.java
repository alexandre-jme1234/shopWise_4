package com.shopwise.app.dto.response;

import com.shopwise.app.entity.LigneVente;

public record LigneVenteResponse(Long id, Long productId, Integer quantiteProduit, Integer prix) {

	public static LigneVenteResponse from(LigneVente ligne) {
		return new LigneVenteResponse(
				ligne.getId(),
				ligne.getProduct().getId(),
				ligne.getQuantiteProduit(),
				ligne.getPrix());
	}
}
