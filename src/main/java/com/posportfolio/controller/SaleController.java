package com.posportfolio.controller;

import com.posportfolio.dto.CartItemDto;
import com.posportfolio.dto.CreateSaleRequest;
import com.posportfolio.dto.SaleDto;
import com.posportfolio.dto.SaleItemDto;
import com.posportfolio.model.SaleEntity;
import com.posportfolio.model.SaleItemEntity;
import com.posportfolio.repository.ProductRepository;
import com.posportfolio.repository.SaleRepository;
import com.posportfolio.repository.SaleItemRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleRepository saleRepository;

    private final SaleItemRepository saleItemRepository;
    private final com.posportfolio.repository.ProductRepository productRepository;

    public SaleController(SaleRepository saleRepository, SaleItemRepository saleItemRepository, com.posportfolio.repository.ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.productRepository = productRepository;
    }


    @PostMapping
    @Transactional
    public ResponseEntity<?> createSale(@RequestBody CreateSaleRequest request, Authentication authentication) {
        if (request.items() == null || request.items().isEmpty()) {
            return ResponseEntity.badRequest().body("Cart cannot be empty");
        }




        if (request.paymentMethod() == null || 
            (!request.paymentMethod().equals("CASH") && !request.paymentMethod().equals("CARD"))) {
            return ResponseEntity.badRequest().body("Invalid payment method");
        }

        try {
            String username = authentication.getName();
            double totalAmount = request.items().stream()
                    .mapToDouble(item -> item.price() * item.quantity())
                    .sum();

            // Generate unique receipt number
            String receiptNumber = "RCP-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            SaleEntity sale = new SaleEntity(receiptNumber, username, totalAmount, request.paymentMethod());
            SaleEntity savedSale = saleRepository.save(sale);

            // Aggregate quantities per productId (important if the same product appears in multiple cart lines)
            var quantityByProductId = request.items().stream()
                    .collect(Collectors.groupingBy(
                            CartItemDto::productId,
                            Collectors.summingInt(CartItemDto::quantity)
                    ));

            // Validate stock once per product
            for (var entry : quantityByProductId.entrySet()) {
                Long productId = entry.getKey();
                int requiredQty = entry.getValue();

                var productOpt = productRepository.findById(productId);
                if (productOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body("Product not found: " + productId);
                }

                var product = productOpt.get();
                if (product.getQuantity() < requiredQty) {
                    return ResponseEntity.badRequest().body("Insufficient stock for product: " + product.getName());
                }
            }

            // Subtract stock once per product (still within @Transactional)
            for (var entry : quantityByProductId.entrySet()) {
                Long productId = entry.getKey();
                int requiredQty = entry.getValue();

                var product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

                product.setQuantity(product.getQuantity() - requiredQty);
                productRepository.save(product);
            }

            // Create sale items (one row per cart line)
            List<SaleItemEntity> items = request.items().stream()
                    .map(cartItem -> {
                        SaleItemEntity item = new SaleItemEntity(
                                cartItem.productId(),
                                cartItem.productName(),
                                cartItem.quantity(),
                                cartItem.price(),
                                cartItem.price() * cartItem.quantity()
                        );
                        item.setSale(savedSale);
                        return item;
                    })
                    .collect(Collectors.toList());


            saleItemRepository.saveAll(items);
            savedSale.setItems(items);

            return ResponseEntity.ok(convertToDto(savedSale));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create sale: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<SaleDto>> getAllSales() {
        List<SaleEntity> sales = saleRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(sales.stream().map(this::convertToDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSaleById(@PathVariable Long id) {
        return saleRepository.findById(id)
                .map(sale -> ResponseEntity.ok(convertToDto(sale)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private SaleDto convertToDto(SaleEntity sale) {
        List<SaleItemDto> itemDtos = sale.getItems().stream()
                .map(item -> new SaleItemDto(
                        item.getId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new SaleDto(
                sale.getId(),
                sale.getReceiptNumber(),
                sale.getUsername(),
                sale.getTotalAmount(),
                sale.getPaymentMethod(),
                sale.getCreatedAt(),
                sale.getStatus(),
                itemDtos
        );
    }
}
