package com.posportfolio.controller;

import com.posportfolio.dto.ProductDto;
import com.posportfolio.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> listProducts(@RequestParam(value = "search", required = false) String search) {
        String filter = search != null ? search.trim() : "";
        List<ProductDto> products = (filter.isBlank()
                ? productRepository.findAll()
                : productRepository.findByNameContainingIgnoreCase(filter))
                .stream()
                .map(product -> new ProductDto(product.getId(), product.getName(), product.getPrice(), product.getQuantity()))
                .toList();
        return ResponseEntity.ok(products);
    }
}
