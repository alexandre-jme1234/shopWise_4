package com.shopwise.app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopwise.app.dto.request.DiscountRequest;
import com.shopwise.app.dto.response.DiscountResponse;
import com.shopwise.app.entity.Discount;
import com.shopwise.app.exception.NotFoundException;
import com.shopwise.app.repository.DiscountRepository;

@Service
@Transactional
public class DiscountService {

	private final DiscountRepository discountRepository;

	public DiscountService(DiscountRepository discountRepository) {
		this.discountRepository = discountRepository;
	}

	public DiscountResponse create(DiscountRequest request) {
		Discount saved = discountRepository.save(new Discount(request.reduce()));
		return DiscountResponse.from(saved);
	}

	@Transactional(readOnly = true)
	public List<DiscountResponse> getAll() {
		return discountRepository.findAll().stream()
				.map(DiscountResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public DiscountResponse getById(Long id) {
		return DiscountResponse.from(find(id));
	}

	public DiscountResponse update(Long id, DiscountRequest request) {
		Discount discount = find(id);
		discount.setReduce(request.reduce());
		return DiscountResponse.from(discountRepository.save(discount));
	}

	public void delete(Long id) {
		discountRepository.delete(find(id));
		// flush immédiat pour que une violation de clé étrangère soit levée ici
		discountRepository.flush();
	}

	private Discount find(Long id) {
		return discountRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Discount not found"));
	}

}
