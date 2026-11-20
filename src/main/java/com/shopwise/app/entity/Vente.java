package com.shopwise.app.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/*
 * Racine de composition : Vente possède ses LigneVente.
 * Les lignes ne sont créées et supprimées que par cette classe.
 */
@Entity
@Table(name = "VENTES")
public class Vente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// orphanRemoval : une ligne retirée de la liste est supprimée en base
	@OneToMany(mappedBy = "vente", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<LigneVente> lignes = new ArrayList<>();

	// FK vers Boutique : pas encore d'entité Boutique, simple colonne pour l'instant
	@Column(name = "boutique_id", nullable = false)
	private Long boutiqueId;

	// FK vers DISCOUNT : optionnelle, toutes les ventes n'ont pas de réduction
	@ManyToOne
	@JoinColumn(name = "discount_id")
	private Discount discount;

	// Acheteur de la vente. Nullable : les ventes créées sans acheteur restent acceptées.
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "date_achat", nullable = false)
	private LocalDate dateAchat;

	// Calculés à partir des lignes (voir VenteService.recalculerTotaux)
	@Column(name = "quantite_totale", nullable = false)
	private Integer quantiteTotale = 0;

	@Column(nullable = false)
	private Integer total = 0;

	@Column(nullable = false)
	private Float prix = 0f;

	// Requis par JPA uniquement
	protected Vente() {
	}

	public Vente(Long boutiqueId, Discount discount, LocalDate dateAchat) {
		this.boutiqueId = boutiqueId;
		this.discount = discount;
		this.dateAchat = dateAchat;
	}

	// Seul moyen de créer une ligne : elle est rattachée à cette vente
	public void ajouterLigne(Product product, Integer quantiteProduit, Integer prix) {
		lignes.add(new LigneVente(this, product, quantiteProduit, prix));
	}

	// Remplace une ligne par une nouvelle, à la même position (l'ancienne est supprimée via orphanRemoval)
	public void remplacerLigne(Long ligneId, Product product, Integer quantiteProduit, Integer prix) {
		for (int i = 0; i < lignes.size(); i++) {
			if (ligneId.equals(lignes.get(i).getId())) {
				lignes.set(i, new LigneVente(this, product, quantiteProduit, prix));
				return;
			}
		}
	}

	// Retire une ligne de la vente (elle disparaît en base via orphanRemoval)
	public void supprimerLigne(Long ligneId) {
		lignes.removeIf(ligne -> ligneId.equals(ligne.getId()));
	}

	// Supprime toutes les lignes (elles disparaissent en base via orphanRemoval)
	public void viderLignes() {
		lignes.clear();
	}

	public Long getId() {
		return id;
	}

	// Vue non modifiable : on ne peut pas ajouter de ligne en contournant ajouterLigne
	public List<LigneVente> getLignes() {
		return Collections.unmodifiableList(lignes);
	}

	public Long getBoutiqueId() {
		return boutiqueId;
	}

	public void setBoutiqueId(Long boutiqueId) {
		this.boutiqueId = boutiqueId;
	}

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDate getDateAchat() {
		return dateAchat;
	}

	public void setDateAchat(LocalDate dateAchat) {
		this.dateAchat = dateAchat;
	}

	public Integer getQuantiteTotale() {
		return quantiteTotale;
	}

	public void setQuantiteTotale(Integer quantiteTotale) {
		this.quantiteTotale = quantiteTotale;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Float getPrix() {
		return prix;
	}

	public void setPrix(Float prix) {
		this.prix = prix;
	}

}
