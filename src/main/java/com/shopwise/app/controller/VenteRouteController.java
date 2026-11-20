package com.shopwise.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopwise.app.dto.request.CreerVenteRequest;
import com.shopwise.app.dto.request.VenteRequest;
import com.shopwise.app.dto.response.VenteResponse;
import com.shopwise.app.service.VenteService;

import jakarta.validation.Valid;

// Routes de vente à chemin libre (sans préfixe /api/ventes)
@RestController
public class VenteRouteController {

	private final VenteService venteService;

	public VenteRouteController(VenteService venteService) {
		this.venteService = venteService;
	}

	// Crée une vente pour un produit et un utilisateur
	@PostMapping("/creer-vente")
	public ResponseEntity<VenteResponse> creerVente(@Valid @RequestBody CreerVenteRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(venteService.creerVente(request));
	}

	// Modifie la vente identifiée par venteId (remplace l'en-tête et les lignes)
	@PutMapping("/modifier-vente")
	public ResponseEntity<VenteResponse> modifierVente(@RequestParam Long venteId,
			@Valid @RequestBody VenteRequest request) {
		return ResponseEntity.ok(venteService.update(venteId, request));
	}

}
