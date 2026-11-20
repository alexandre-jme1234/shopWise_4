package com.shopwise.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/*
 * Composition : une LigneVente n'existe que dans une Vente.
 * - Le constructeur est package-private : seule Vente.ajouterLigne(...) peut l'instancier.
 * - Pas de setters : une ligne se remplace, elle ne se modifie pas.
 * - vente_id est obligatoire.
 */
@Entity
@Table(name = "LIGNE_VENTE")
public class LigneVente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "vente_id", nullable = false)
	private Vente vente;

	// Relation unidirectionnelle : la ligne connaît son produit, le produit ne connaît pas ses lignes
	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(name = "quantite_produit", nullable = false)
	private Integer quantiteProduit;

	@Column(nullable = false)
	private Integer prix;

	// Requis par JPA uniquement
	protected LigneVente() {
	}

	LigneVente(Vente vente, Product product, Integer quantiteProduit, Integer prix) {
		this.vente = vente;
		this.product = product;
		this.quantiteProduit = quantiteProduit;
		this.prix = prix;
	}

	public Long getId() {
		return id;
	}

	public Vente getVente() {
		return vente;
	}

	public Product getProduct() {
		return product;
	}

	public Integer getQuantiteProduit() {
		return quantiteProduit;
	}

	public Integer getPrix() {
		return prix;
	}

}
