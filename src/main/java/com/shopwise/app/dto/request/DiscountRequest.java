package com.shopwise.app.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record DiscountRequest(
		@NotNull(message = "La réduction est obligatoire")
		@PositiveOrZero(message = "La réduction doit être positive ou nulle")
		Float reduce) {
}
