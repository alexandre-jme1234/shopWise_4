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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopwise.app.dto.request.DiscountRequest;
import com.shopwise.app.dto.response.DiscountResponse;
import com.shopwise.app.service.DiscountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

	private final DiscountService discountService;

	public DiscountController(DiscountService discountService) {
		this.discountService = discountService;
	}

	@PostMapping
	public ResponseEntity<DiscountResponse> create(@Valid @RequestBody DiscountRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(discountService.create(request));
	}

	@GetMapping
	public ResponseEntity<List<DiscountResponse>> getAll() {
		return ResponseEntity.ok(discountService.getAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DiscountResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(discountService.getById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<DiscountResponse> update(@PathVariable Long id,
			@Valid @RequestBody DiscountRequest request) {
		return ResponseEntity.ok(discountService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		discountService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
