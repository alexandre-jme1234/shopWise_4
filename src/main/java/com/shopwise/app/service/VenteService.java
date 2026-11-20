package com.shopwise.app.service;

import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopwise.app.dto.request.CreerVenteRequest;
import com.shopwise.app.dto.request.LigneVenteRequest;
import com.shopwise.app.dto.request.VenteRequest;
import com.shopwise.app.dto.response.VenteResponse;
import com.shopwise.app.entity.Discount;
import com.shopwise.app.entity.LigneVente;
import com.shopwise.app.entity.Product;
import com.shopwise.app.entity.User;
import com.shopwise.app.entity.Vente;
import com.shopwise.app.exception.NotFoundException;
import com.shopwise.app.exception.VenteException;
import com.shopwise.app.repository.DiscountRepository;
import com.shopwise.app.repository.ProductRepository;
import com.shopwise.app.repository.UserRepository;
import com.shopwise.app.repository.VenteRepository;

@Service
@Transactional
public class VenteService {

	private final VenteRepository venteRepository;
	private final ProductRepository productRepository;
	private final DiscountRepository discountRepository;
	private final UserRepository userRepository;

	public VenteService(VenteRepository venteRepository,
			ProductRepository productRepository,
			DiscountRepository discountRepository,
			UserRepository userRepository) {
		this.venteRepository = venteRepository;
		this.productRepository = productRepository;
		this.discountRepository = discountRepository;
		this.userRepository = userRepository;
	}

	/**
	 * Crée une vente d'un produit pour un utilisateur.
	 * Le prix unitaire est celui du produit, arrondi à l'entier (LigneVente.prix est un Integer).
	 */
	public VenteResponse creerVente(CreerVenteRequest request) {
		Product product = findProduct(request.productId());
		User acheteur = userRepository.findById(request.userId())
				.orElseThrow(() -> new NotFoundException("User not found"));

		Vente vente = new Vente(request.boutiqueId(), findDiscount(request.discountId()), request.dateAchat());
		vente.setUser(acheteur);
		vente.ajouterLigne(product, request.quantiteProduit(),
				product.getPrice().setScale(0, RoundingMode.HALF_UP).intValueExact());
		recalculerTotaux(vente);
		return VenteResponse.from(venteRepository.save(vente));
	}

	public VenteResponse create(VenteRequest request) {
		Vente vente = new Vente(request.boutiqueId(), findDiscount(request.discountId()), request.dateAchat());
		ajouterLignes(vente, request.lignes());
		recalculerTotaux(vente);
		return VenteResponse.from(venteRepository.save(vente));
	}

	@Transactional(readOnly = true)
	public List<VenteResponse> getAll(String sort) {
		return venteRepository.findAll(parseSort(sort)).stream()
				.map(VenteResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public VenteResponse getById(Long id) {
		return VenteResponse.from(find(id));
	}

	/**
	 * Met à jour l'en-tête et remplace les lignes ; quantité, total et prix
	 * sont recalculés automatiquement à partir des nouvelles lignes.
	 */
	public VenteResponse update(Long id, VenteRequest request) {
		Vente vente = find(id);
		vente.setBoutiqueId(request.boutiqueId());
		vente.setDiscount(findDiscount(request.discountId()));
		vente.setDateAchat(request.dateAchat());
		vente.viderLignes();
		ajouterLignes(vente, request.lignes());
		recalculerTotaux(vente);
		return VenteResponse.from(venteRepository.save(vente));
	}

	public void delete(Long id) {
		venteRepository.delete(find(id));
		venteRepository.flush();
	}

	// Les lignes sont créées par la vente elle-même (composition)
	private void ajouterLignes(Vente vente, List<LigneVenteRequest> requests) {
		for (LigneVenteRequest r : requests) {
			vente.ajouterLigne(findProduct(r.productId()), r.quantiteProduit(), r.prix());
		}
	}

	private Product findProduct(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Product not found"));
	}

	/**
	 * Recalcule à partir des lignes de la vente :
	 *  - quantiteTotale = somme des quantités
	 *  - total          = somme de (quantité × prix unitaire)
	 *  - prix           = somme des prix des lignes (prix cumulé)
	 */
	private void recalculerTotaux(Vente vente) {
		int quantiteTotale = 0;
		int total = 0;
		float prixCumule = 0f;

		for (LigneVente ligne : vente.getLignes()) {
			quantiteTotale += ligne.getQuantiteProduit();
			total += ligne.getQuantiteProduit() * ligne.getPrix();
			prixCumule += ligne.getPrix();
		}

		vente.setQuantiteTotale(quantiteTotale);
		vente.setTotal(total);
		vente.setPrix(prixCumule);
	}

	/**
	 * Supprime une ligne d'une vente : la quantité et le total sont recalculés.
	 */
	public VenteResponse supprimerLigne(Long venteId, Long ligneId) {
		Vente vente = find(venteId);
		ligneDansVente(vente, ligneId);
		vente.supprimerLigne(ligneId);
		recalculerTotaux(vente);
		return VenteResponse.from(venteRepository.save(vente));
	}

	public VenteResponse remplacerLigne(Long venteId, Long ligneId, LigneVenteRequest request) {
		Vente vente = find(venteId);
		ligneDansVente(vente, ligneId);
		vente.remplacerLigne(ligneId, findProduct(request.productId()),
				request.quantiteProduit(), request.prix());
		recalculerTotaux(vente);
		return VenteResponse.from(venteRepository.save(vente));
	}

	private LigneVente ligneDansVente(Vente vente, Long ligneId) {
		return vente.getLignes().stream()
				.filter(ligne -> ligneId.equals(ligne.getId()))
				.findFirst()
				.orElseThrow(() -> VenteException.ligneIntrouvableDansVente(vente.getId(), ligneId));
	}

	// Champs sur lesquels on peut trier : "dateAchat,asc" ou "total,desc"
	private static final Set<String> CHAMPS_TRIABLES =
			Set.of("id", "dateAchat", "total", "quantiteTotale", "prix", "boutiqueId");

	private Sort parseSort(String sort) {
		String[] parts = sort.split(",");
		String champ = parts[0].trim();
		if (!CHAMPS_TRIABLES.contains(champ)) {
			throw new IllegalArgumentException("Tri impossible sur le champ : " + champ);
		}
		Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())
				? Sort.Direction.DESC
				: Sort.Direction.ASC;
		return Sort.by(direction, champ);
	}

	private Vente find(Long id) {
		return venteRepository.findById(id)
				.orElseThrow(() -> VenteException.venteIntrouvable(id));
	}

	// Ligne d'une vente précise : erreur si la vente n'existe pas ou si la ligne n'y appartient pas
	public LigneVente trouverLigne(Long venteId, Long ligneVenteId) {
		return find(venteId).getLignes().stream()
				.filter(ligne -> ligne.getId().equals(ligneVenteId))
				.findFirst()
				.orElseThrow(() -> VenteException.ligneIntrouvableDansVente(venteId, ligneVenteId));
	}

	// Retourne null si aucune réduction n'est demandée
	private Discount findDiscount(Long id) {
		if (id == null) {
			return null;
		}
		return discountRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Discount not found"));
	}

}
