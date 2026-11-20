package com.shopwise.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopwise.app.dto.request.LigneVenteRequest;
import com.shopwise.app.dto.request.VenteRequest;
import com.shopwise.app.dto.response.VenteResponse;
import com.shopwise.app.service.VenteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ventes")
public class VenteController {

	private final VenteService venteService;

	public VenteController(VenteService venteService) {
		this.venteService = venteService;
	}

	@PostMapping
	public ResponseEntity<VenteResponse> create(@Valid @RequestBody VenteRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(venteService.create(request));
	}

	@GetMapping
	public ResponseEntity<List<VenteResponse>> getAll(
			@RequestParam(defaultValue = "dateAchat,asc") String sort) {
		return ResponseEntity.ok(venteService.getAll(sort));
	}

	@GetMapping("/{id}")
	public ResponseEntity<VenteResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(venteService.getById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<VenteResponse> update(@PathVariable Long id,
			@Valid @RequestBody VenteRequest request) {
		return ResponseEntity.ok(venteService.update(id, request));
	}

	// Supprime une ligne d'une vente ; quantité et total recalculés
	@DeleteMapping("/{venteId}/lignes/{ligneId}")
	public ResponseEntity<VenteResponse> supprimerLigne(@PathVariable Long venteId, @PathVariable Long ligneId) {
		return ResponseEntity.ok(venteService.supprimerLigne(venteId, ligneId));
	}

	// Remplace une ligne d'une vente par une autre ; quantité et total recalculés
	@PutMapping("/{venteId}/lignes/{ligneId}")
	public ResponseEntity<VenteResponse> remplacerLigne(@PathVariable Long venteId, @PathVariable Long ligneId,
			@Valid @RequestBody LigneVenteRequest request) {
		return ResponseEntity.ok(venteService.remplacerLigne(venteId, ligneId, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		venteService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
