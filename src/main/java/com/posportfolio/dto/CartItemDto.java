package com.posportfolio.dto;

public record CartItemDto(
    Long productId,
    String productName,
    int quantity,
    double price
) {
}
