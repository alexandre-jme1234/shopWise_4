package com.shopwise.app.exception;

import org.springframework.http.HttpStatus;

/**
 * Erreur liée à une vente ou à une de ses lignes.
 * Transporte les identifiants concernés pour que la réponse JSON les renvoie.
 */
public class VenteException extends RuntimeException {

	private final HttpStatus status;
	private final Long venteId;
	private final Long ligneVenteId;

	public VenteException(HttpStatus status, String message, Long venteId, Long ligneVenteId) {
		super(message);
		this.status = status;
		this.venteId = venteId;
		this.ligneVenteId = ligneVenteId;
	}

	public static VenteException venteIntrouvable(Long venteId) {
		return new VenteException(HttpStatus.NOT_FOUND,
				"Vente " + venteId + " introuvable", venteId, null);
	}

	public static VenteException ligneIntrouvableDansVente(Long venteId, Long ligneVenteId) {
		return new VenteException(HttpStatus.NOT_FOUND,
				"Ligne " + ligneVenteId + " introuvable dans la vente " + venteId, venteId, ligneVenteId);
	}

	public HttpStatus getStatus() {
		return status;
	}

	public Long getVenteId() {
		return venteId;
	}

	public Long getLigneVenteId() {
		return ligneVenteId;
	}

}
