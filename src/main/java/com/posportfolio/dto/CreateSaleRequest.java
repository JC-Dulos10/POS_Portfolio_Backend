package com.posportfolio.dto;

import java.util.List;

public record CreateSaleRequest(
    String paymentMethod,
    List<CartItemDto> items
) {
}
