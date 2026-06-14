package com.posportfolio.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SaleDto(
    Long id,
    String receiptNumber,
    String username,
    double totalAmount,
    String paymentMethod,
    LocalDateTime createdAt,
    String status,
    List<SaleItemDto> items
) {
}
