package com.posportfolio.dto;

public record SaleItemDto(
    Long id,
    Long productId,
    String productName,
    int quantity,
    double price,
    double subtotal
) {
}
