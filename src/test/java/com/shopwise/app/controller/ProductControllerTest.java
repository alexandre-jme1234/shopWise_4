package com.shopwise.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;
import com.shopwise.app.AbstractApiTest;

/**
 * TDD : CRUD des produits réservé au COMMERCANT.
 * 401 sans authentification, 403 pour un CLIENT.
 */
@DisplayName("ProductController - TDD")
class ProductControllerTest extends AbstractApiTest {

	private static final String PRODUCTS = "/api/products";

	private static final String CORPS_PRODUIT = """
			{"name": "Chemise", "description": "Coton bio", "price": 29.9}
			""";

	private static final String CORPS_PRODUIT_MODIFIE = """
			{"name": "Chemise v2", "description": "Coton bio", "price": 34.9}
			""";

	// ---------------------------------------------------------------------
	// COMMERCANT : CRUD complet
	// ---------------------------------------------------------------------

	@Test
	@DisplayName("COMMERCANT crée, lit, modifie puis supprime un produit")
	void commercant_fait_le_crud_complet() throws Exception {
		// Create -> 201
		MvcResult creation = mockMvc.perform(post(PRODUCTS).with(authCommercant())
						.contentType(MediaType.APPLICATION_JSON)
						.content(CORPS_PRODUIT))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("Chemise"))
				.andReturn();
		int id = JsonPath.read(creation.getResponse().getContentAsString(), "$.id");

		// Read -> 200
		mockMvc.perform(get(PRODUCTS + "/{id}", id).with(authCommercant()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Chemise"));

		// Update -> 200
		mockMvc.perform(put(PRODUCTS + "/{id}", id).with(authCommercant())
						.contentType(MediaType.APPLICATION_JSON)
						.content(CORPS_PRODUIT_MODIFIE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Chemise v2"))
				.andExpect(jsonPath("$.price").value(34.9));

		// Delete -> 204, puis introuvable -> 404
		mockMvc.perform(delete(PRODUCTS + "/{id}", id).with(authCommercant()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get(PRODUCTS + "/{id}", id).with(authCommercant()))
				.andExpect(status().isNotFound());
	}

	// ---------------------------------------------------------------------
	// Sans authentification : 401
	// ---------------------------------------------------------------------

	@Test
	@DisplayName("Sans authentification, création de produit -> 401")
	void anonyme_ne_peut_pas_creer_un_produit() throws Exception {
		mockMvc.perform(post(PRODUCTS)
						.contentType(MediaType.APPLICATION_JSON)
						.content(CORPS_PRODUIT))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("Sans authentification, lecture de produits -> 401")
	void anonyme_ne_peut_pas_lire_les_produits() throws Exception {
		mockMvc.perform(get(PRODUCTS))
				.andExpect(status().isUnauthorized());
	}

	// ---------------------------------------------------------------------
	// CLIENT : 403 sur toutes les opérations
	// ---------------------------------------------------------------------

	@Test
	@DisplayName("CLIENT ne peut pas créer de produit -> 403")
	void client_ne_peut_pas_creer_un_produit() throws Exception {
		mockMvc.perform(post(PRODUCTS).with(authClient())
						.contentType(MediaType.APPLICATION_JSON)
						.content(CORPS_PRODUIT))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("CLIENT ne peut pas lire les produits -> 403")
	void client_ne_peut_pas_lire_les_produits() throws Exception {
		mockMvc.perform(get(PRODUCTS).with(authClient()))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("CLIENT ne peut pas modifier un produit -> 403")
	void client_ne_peut_pas_modifier_un_produit() throws Exception {
		mockMvc.perform(put(PRODUCTS + "/{id}", 1).with(authClient())
						.contentType(MediaType.APPLICATION_JSON)
						.content(CORPS_PRODUIT_MODIFIE))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("CLIENT ne peut pas supprimer un produit -> 403")
	void client_ne_peut_pas_supprimer_un_produit() throws Exception {
		mockMvc.perform(delete(PRODUCTS + "/{id}", 1).with(authClient()))
				.andExpect(status().isForbidden());
	}

}
