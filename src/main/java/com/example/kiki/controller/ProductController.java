package com.example.kiki.controller;

import com.example.kiki.dto.product.ProductRequestDto;
import com.example.kiki.dto.product.ProductResponseDto;
import com.example.kiki.service.ImageUploadService;
import com.example.kiki.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ImageUploadService imageUploadService;

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
            @RequestParam(required = false) String search,
            Pageable pageable) {

        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(productService.searchProducts(search, pageable));
        }
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZATION')")
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto request) {
        ProductResponseDto created = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZATION')")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @PostMapping("/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZATION')")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = imageUploadService.uploadImage(file);
        return ResponseEntity.ok(Map.of("imageUrl", url));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZATION')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}