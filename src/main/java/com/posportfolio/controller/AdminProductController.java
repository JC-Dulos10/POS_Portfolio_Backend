package com.posportfolio.controller;

import com.posportfolio.dto.ProductDto;
import com.posportfolio.model.ProductEntity;
import com.posportfolio.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductRepository productRepository;

    public AdminProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> listProducts() {
        List<ProductDto> products = productRepository.findAll().stream()
                .map(product -> new ProductDto(product.getId(), product.getName(), product.getPrice(), product.getQuantity()))
                .toList();
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductDto request) {
        if (request.name() == null || request.name().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Product name is required");
        }
        if (request.price() < 0) {
            return ResponseEntity.badRequest().body("Product price must be zero or positive");
        }
        if (request.quantity() < 0) {
            return ResponseEntity.badRequest().body("Product quantity must be zero or positive");
        }

        ProductEntity saved = productRepository.save(new ProductEntity(request.name().trim(), request.price(), request.quantity()));
        return ResponseEntity.ok(new ProductDto(saved.getId(), saved.getName(), saved.getPrice(), saved.getQuantity()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") Long id, @RequestBody ProductDto request) {
        return productRepository.findById(id)
                .map(product -> {
                    if (request.name() == null || request.name().trim().isEmpty()) {
                        return ResponseEntity.badRequest().body("Product name is required");
                    }
                    if (request.price() < 0) {
                        return ResponseEntity.badRequest().body("Product price must be zero or positive");
                    }
                    if (request.quantity() < 0) {
                        return ResponseEntity.badRequest().body("Product quantity must be zero or positive");
                    }
                    product.setName(request.name().trim());
                    product.setPrice(request.price());
                    product.setQuantity(request.quantity());
                    ProductEntity updated = productRepository.save(product);
                    return ResponseEntity.ok(new ProductDto(updated.getId(), updated.getName(), updated.getPrice(), updated.getQuantity()));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
}
