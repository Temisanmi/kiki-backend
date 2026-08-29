package com.example.kiki.service;

import com.example.kiki.dto.product.ProductRequestDto;
import com.example.kiki.dto.product.ProductResponseDto;
import com.example.kiki.entity.Organization;
import com.example.kiki.entity.Product;
import com.example.kiki.exception.ForbiddenOperationException;
import com.example.kiki.exception.ResourceNotFoundException;
import com.example.kiki.repository.OrganizationRepository;
import com.example.kiki.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final OrganizationRepository organizationRepository;

    private ProductResponseDto toResponseDto(Product product) {
        Organization organization = product.getOrganization();

        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.getStockQuantity(),
                organization != null ? organization.getOrgName() : null,  //tenary operator
                organization != null ? organization.getId() : null
        );
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
    }
    public ProductResponseDto createProduct(ProductRequestDto request) {
        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl());
        product.setStockQuantity(request.getStockQuantity());
        if (!isAdmin()) {
            product.setOrganization(getCurrentOrganization());
        }

        Product saved = productRepository.save(product);
        return toResponseDto(saved);
    }

    private Pageable withDefaultSort(Pageable pageable){
        if(pageable.getSort().isUnsorted()){
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        return pageable;
    }

    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        pageable = withDefaultSort(pageable);

        if (isAdmin()){
            return productRepository.findAll(pageable)
                    .map(this::toResponseDto);
        }
        return productRepository.findAllVisible(pageable)
                .map(this::toResponseDto);
    }

    public Page<ProductResponseDto> searchProducts(String keyword, Pageable pageable) {
        pageable = withDefaultSort(pageable);

        if (isAdmin()){
            return productRepository.findByNameContainingIgnoreCase(keyword, pageable)
                    .map(this::toResponseDto);
        }
        return productRepository.searchVisible(keyword, pageable)
                .map(this::toResponseDto);
    }

    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return toResponseDto(product);
    }

    private Organization getCurrentOrganization() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return organizationRepository.findByUser_Username(username)
                .orElseThrow(() -> new ForbiddenOperationException("No organization profile found for this account"));
    }
    public List<ProductResponseDto> getMyProducts() {
        Organization organization = getCurrentOrganization();
        return productRepository.findByOrganization_Id(organization.getId())
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    private void assertCanModify(Product product) {
        if (isAdmin()) {
            return;
        }

        Organization current = getCurrentOrganization();

        if (product.getOrganization() == null || !product.getOrganization().getId().equals(current.getId())) {
            throw new ForbiddenOperationException("You do not have permission to modify this product");
        }
    }
    public ProductResponseDto updateProduct(Long id, ProductRequestDto request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        assertCanModify(product);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl());
        product.setStockQuantity(request.getStockQuantity());

        Product updated = productRepository.save(product);
        return toResponseDto(updated);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        assertCanModify(product);
        productRepository.delete(product);
    }
}