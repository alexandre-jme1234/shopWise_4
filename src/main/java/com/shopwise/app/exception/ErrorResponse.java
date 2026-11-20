package com.shopwise.app.exception;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Format unique de toutes les réponses d'erreur de l'API.
 *
 * @param timestamp   moment de l'erreur (ISO-8601)
 * @param status      code HTTP (ex. 404)
 * @param error       libellé HTTP (ex. "Not Found")
 * @param message     description lisible de l'erreur
 * @param path        URL de la requête qui a échoué
 * @param venteId     id de la vente concernée, null sinon
 * @param ligneVenteId id de la ligne de vente concernée, null sinon
 * @param fieldErrors erreurs par champ (validation uniquement), absent sinon
 */
public record ErrorResponse(
		Instant timestamp,
		int status,
		String error,
		String message,
		String path,
		Long venteId,
		Long ligneVenteId,
		@JsonInclude(JsonInclude.Include.NON_NULL)
		Map<String, String> fieldErrors) {

	public static ErrorResponse of(HttpStatus status, String message, String path) {
		return new ErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(),
				message, path, null, null, null);
	}

	public static ErrorResponse ofVente(HttpStatus status, String message, String path,
			Long venteId, Long ligneVenteId) {
		return new ErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(),
				message, path, venteId, ligneVenteId, null);
	}

	public static ErrorResponse withFieldErrors(HttpStatus status, String message, String path,
			Map<String, String> fieldErrors) {
		return new ErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(),
				message, path, null, null, fieldErrors);
	}
}
