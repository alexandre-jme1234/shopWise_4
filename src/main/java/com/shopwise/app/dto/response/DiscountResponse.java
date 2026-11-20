package com.shopwise.app.dto.response;

import com.shopwise.app.entity.Discount;

public record DiscountResponse(Long id, Float reduce) {

	public static DiscountResponse from(Discount discount) {
		return new DiscountResponse(discount.getId(), discount.getReduce());
	}
}
