package com.shopwise.app.controller;

import static org.hamcrest.Matchers.contains;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.jayway.jsonpath.JsonPath;
import com.shopwise.app.AbstractApiTest;

/**
 * TDD : contrat attendu de /api/ventes.
 * Hypothèses à valider avant implémentation :
 *  - total d'une vente = somme de (quantiteProduit * prix) de ses lignes
 *  - la réponse de suppression et de remplacement renvoie la vente mise à jour (200)
 *  - POST /api/ventes renvoie 200 (demande explicite)
 */
@DisplayName("VenteController - TDD")
class VenteControllerTest extends AbstractApiTest {

	private static final String VENTES = "/api/ventes";

	// total attendu : 2 * 1000 + 1 * 500 = 2500
	private static final String CORPS_VENTE = corpsVente("2026-01-10");

	private static String corpsVente(String dateAchat) {
		return """
				{
				  "boutiqueId": 1,
				  "dateAchat": "%s",
				  "lignes": [
				    {"productId": 1, "quantiteProduit": 2, "prix": 1000},
				    {"productId": 2, "quantiteProduit": 1, "prix": 500}
				  ]
				}
				""".formatted(dateAchat);
	}

	// Crée une vente en tant que COMMERCANT et renvoie sa réponse
	private MvcResult creerVente(RequestPostProcessor auth, String corps) throws Exception {
		return mockMvc.perform(post(VENTES).with(auth)
						.contentType(MediaType.APPLICATION_JSON)
						.content(corps))
				.andExpect(status().isCreated())
				.andReturn();
	}

	private int idDe(MvcResult result) throws Exception {
		return JsonPath.read(result.getResponse().getContentAsString(), "$.id");
	}

	private int idLigne(MvcResult result, int index) throws Exception {
		return JsonPath.read(result.getResponse().getContentAsString(), "$.lignes[" + index + "].id");
	}

	// ---------------------------------------------------------------------
	// 1. Enregistrer une vente avec ses lignes (CLIENT et COMMERCANT)
	// ---------------------------------------------------------------------

	@Test
	@DisplayName("CLIENT enregistre une vente avec ses lignes -> 201, total calculé")
	void client_enregistre_une_vente() throws Exception {
		mockMvc.perform(post(VENTES).with(authClient())
						.contentType(MediaType.APPLICATION_JSON)
						.content(CORPS_VENTE))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.lignes.length()").value(2))
				.andExpect(jsonPath("$.lignes[0].productId").value(1))
				.andExpect(jsonPath("$.total").value(2500));
	}

	@Test
	@DisplayName("COMMERCANT enregistre une vente avec ses lignes -> 201, total calculé")
	void commercant_enregistre_une_vente() throws Exception {
		mockMvc.perform(post(VENTES).with(authCommercant())
						.contentType(MediaType.APPLICATION_JSON)
						.content(CORPS_VENTE))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.lignes.length()").value(2))
				.andExpect(jsonPath("$.total").value(2500));
	}

	// ---------------------------------------------------------------------
	// 2. Tri des ventes par date d'achat (dans le service, exposé par GET)
	// ---------------------------------------------------------------------

	private void creerTroisVentesDatees() throws Exception {
		creerVente(authCommercant(), corpsVente("2026-02-20"));
		creerVente(authCommercant(), corpsVente("2026-01-10"));
		creerVente(authCommercant(), corpsVente("2026-03-05"));
	}

	@Test
	@DisplayName("GET /api/ventes?sort=dateAchat,asc -> dates croissantes")
	void trier_ventes_date_croissante() throws Exception {
		creerTroisVentesDatees();

		mockMvc.perform(get(VENTES).param("sort", "dateAchat,asc").with(authCommercant()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].dateAchat")
						.value(contains("2026-01-10", "2026-02-20", "2026-03-05")));
	}

	@Test
	@DisplayName("GET /api/ventes?sort=dateAchat,desc -> dates décroissantes")
	void trier_ventes_date_decroissante() throws Exception {
		creerTroisVentesDatees();

		mockMvc.perform(get(VENTES).param("sort", "dateAchat,desc").with(authCommercant()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].dateAchat")
						.value(contains("2026-03-05", "2026-02-20", "2026-01-10")));
	}

	// ---------------------------------------------------------------------
	// 3. Supprimer une ligne d'une vente : quantités et total mis à jour
	// ---------------------------------------------------------------------

	@Test
	@DisplayName("COMMERCANT supprime une ligne -> 200, total et lignes mis à jour")
	void commercant_supprime_une_ligne() throws Exception {
		MvcResult vente = creerVente(authCommercant(), CORPS_VENTE);
		int venteId = idDe(vente);
		int ligneASupprimer = idLigne(vente, 1); // ligne à 500 (1 x 500)

		mockMvc.perform(delete(VENTES + "/{venteId}/lignes/{ligneId}", venteId, ligneASupprimer)
						.with(authCommercant()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.lignes.length()").value(1))
				.andExpect(jsonPath("$.lignes[0].quantiteProduit").value(2))
				.andExpect(jsonPath("$.total").value(2000));
	}

	@Test
	@DisplayName("CLIENT ne peut pas supprimer une ligne -> 403")
	void client_ne_peut_pas_supprimer_une_ligne() throws Exception {
		MvcResult vente = creerVente(authCommercant(), CORPS_VENTE);

		mockMvc.perform(delete(VENTES + "/{venteId}/lignes/{ligneId}", idDe(vente), idLigne(vente, 1))
						.with(authClient()))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Sans authentification, suppression d'une ligne -> 401")
	void anonyme_ne_peut_pas_supprimer_une_ligne() throws Exception {
		MvcResult vente = creerVente(authCommercant(), CORPS_VENTE);

		mockMvc.perform(delete(VENTES + "/{venteId}/lignes/{ligneId}", idDe(vente), idLigne(vente, 1)))
				.andExpect(status().isUnauthorized());
	}

	// ---------------------------------------------------------------------
	// 4. Remplacer une ligne d'une vente par une autre : quantités et total mis à jour
	// ---------------------------------------------------------------------

	private static final String CORPS_NOUVELLE_LIGNE = """
			{"productId": 1, "quantiteProduit": 3, "prix": 1000}
			""";

	@Test
	@DisplayName("COMMERCANT remplace une ligne -> 200, quantité et total mis à jour")
	void commercant_remplace_une_ligne() throws Exception {
		MvcResult vente = creerVente(authCommercant(), CORPS_VENTE);
		int venteId = idDe(vente);
		int ligneARemplacer = idLigne(vente, 0); // ligne 2 x 1000

		// nouvelle ligne : 3 x 1000 = 3000, à la place de 2000 ; total = 3000 + 500
		mockMvc.perform(put(VENTES + "/{venteId}/lignes/{ligneId}", venteId, ligneARemplacer)
						.with(authCommercant())
						.contentType(MediaType.APPLICATION_JSON)
						.content(CORPS_NOUVELLE_LIGNE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.lignes.length()").value(2))
				.andExpect(jsonPath("$.lignes[0].quantiteProduit").value(3))
				.andExpect(jsonPath("$.total").value(3500));
	}

	@Test
	@DisplayName("CLIENT ne peut pas remplacer une ligne -> 403")
	void client_ne_peut_pas_remplacer_une_ligne() throws Exception {
		MvcResult vente = creerVente(authCommercant(), CORPS_VENTE);

		mockMvc.perform(put(VENTES + "/{venteId}/lignes/{ligneId}", idDe(vente), idLigne(vente, 0))
						.with(authClient())
						.contentType(MediaType.APPLICATION_JSON)
						.content(CORPS_NOUVELLE_LIGNE))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Sans authentification, remplacement d'une ligne -> 401")
	void anonyme_ne_peut_pas_remplacer_une_ligne() throws Exception {
		MvcResult vente = creerVente(authCommercant(), CORPS_VENTE);

		mockMvc.perform(put(VENTES + "/{venteId}/lignes/{ligneId}", idDe(vente), idLigne(vente, 0))
						.contentType(MediaType.APPLICATION_JSON)
						.content(CORPS_NOUVELLE_LIGNE))
				.andExpect(status().isUnauthorized());
	}

}
