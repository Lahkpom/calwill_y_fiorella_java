package com.calwillyfiorella.service;

import com.calwillyfiorella.exception.ProductAlreadyExistException;
import com.calwillyfiorella.exception.ProductDoesNotExistException;
import com.calwillyfiorella.model.BaseEntity;
import com.calwillyfiorella.model.Product;
import com.calwillyfiorella.model.enums.Category;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.repository.ProductRepository;
import com.calwillyfiorella.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) { this.productRepository = productRepository; }

    public void createProduct(Product product) {
        AuthService.checkActualUserIsAdmin();

        Objects.requireNonNull(product);

        if (product.getId() == null) {
            product.setId(UUID.randomUUID());
            product.setRowStatus(RowStatus.ACTIVE);
            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(null);
        } else {
            validateProductId(product.getId());
            BaseEntity.validateRowStatus(product.getRowStatus());
            BaseEntity.validateCreatedAt(product.getCreatedAt());
        }

        validateCategory(product.getCategory());
        validateProductName(product.getName());
        validateProductShortDesc(product.getShortDesc());
        validateProductLongDesc(product.getLongDesc());

        this.productRepository.save(product);
    }

    public void updateProduct(UUID productId, Product newProductData) {
        AuthService.checkActualUserIsAdmin();

        Objects.requireNonNull(productId);
        Objects.requireNonNull(newProductData);

        Product currentProductData = getProduct(productId);

        validateCategory(newProductData.getCategory());
        validateProductName(newProductData.getName(), productId);
        validateProductShortDesc(newProductData.getShortDesc());
        validateProductLongDesc(newProductData.getLongDesc());
        BaseEntity.validateRowStatus(newProductData.getRowStatus());

        currentProductData.setCategory(newProductData.getCategory());
        currentProductData.setName(newProductData.getName());
        currentProductData.setShortDesc(newProductData.getShortDesc());
        currentProductData.setLongDesc(newProductData.getLongDesc());
        currentProductData.setRowStatus(newProductData.getRowStatus());
    }

    public Product getProduct(String productName) {
        return ifProductExists(this.productRepository.findProduct(productName));
    }

    public Product getProduct(UUID productId) { return ifProductExists(this.productRepository.findProduct(productId)); }

    private Product ifProductExists(Optional<Product> productOptional) {
        return productOptional.orElseThrow(ProductDoesNotExistException::new);
    }

    public List<Product> getAll() { return this.productRepository.findAll(); }

    private void validateProductId(UUID productId) {
        Objects.requireNonNull(productId, "Product ID cannot be null");

        if (this.productRepository.findProduct(productId).isPresent())
            throw new ProductAlreadyExistException("El ID del producto ya existe en la lista!");
    }

    private void validateCategory(Category productCategory) {
        Objects.requireNonNull(productCategory, "Product category cannot be null");
    }

    private void validateProductName(String productName) {
        validateProductName(productName, null);
    }
    private void validateProductName(String productName, UUID currentProductId) {
        ValidationUtils.requireNonBlank(productName, "Product name cannot be blank.");

        Optional<Product> existingProduct = this.productRepository.findProduct(productName);

        if (existingProduct.isPresent() && (currentProductId == null || !existingProduct.get().getId().equals(currentProductId)))
            throw new ProductAlreadyExistException("Ya existe un producto con el nombre " +  productName);
    }

    private void validateProductShortDesc(String productShortDesc) {
        ValidationUtils.requireNonBlank(productShortDesc, "Product short desc cannot be blank.");
    }

    private void validateProductLongDesc(String productLongDesc) {
        ValidationUtils.requireNonBlank(productLongDesc, "Product long desc cannot be blank.");
    }
}
