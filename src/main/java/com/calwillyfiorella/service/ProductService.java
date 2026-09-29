package com.calwillyfiorella.service;

import com.calwillyfiorella.exception.ProductAlreadyExistException;
import com.calwillyfiorella.exception.ProductDoesNotExistException;
import com.calwillyfiorella.model.Product;
import com.calwillyfiorella.model.enums.Category;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.repository.ProductRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) { this.productRepository = productRepository; }

    public void createProduct(
        Category    category,
        String      productName,
        String      productShortDesc,
        String      productLongDesc
    ) {
        AuthService.checkActualUserIsAdmin();

        validateProductName(productName);

        this.productRepository.save(
                new Product(
                        category,
                        productName,
                        productShortDesc,
                        productLongDesc
                )
        );
    }

    private void validateProductName(String productName) {
        if (this.productRepository.findProduct(productName).isPresent())
            throw new ProductAlreadyExistException("El nombre del producto ya existe!");
    }

    public Product getProduct(String productName) {
        return ifProductExists(this.productRepository.findProduct(productName));
    }

    public Product getProduct(UUID productId) {
        return ifProductExists(this.productRepository.findProduct(productId));
    }

    private Product ifProductExists(Optional<Product> productOptional) {
        return productOptional.orElseThrow(ProductDoesNotExistException::new);
    }

    public List<Product> getAll() { return this.productRepository.findAll(); }

    public void updateCategory(Product product, Category category) {
        product.setProductCategory(category);
    }
    public void updateName(UUID productId, String productName) {
        validateProductName(productName);

        ifProductExists(productRepository.findProduct(productId))
                .setProductName(productName);
    }
    public void updateShortDesc(UUID productId, String productShortDesc) {
        Objects.requireNonNull(productShortDesc, "La nueva descripción no puede ser nula.");

        ifProductExists(productRepository.findProduct(productId))
                .setProductShortDesc(productShortDesc);
    }
    public void updateLongDesc(UUID productId, String productLongDesc) {
        Objects.requireNonNull(productLongDesc, "La nueva descripción no puede ser nula.");

        ifProductExists(productRepository.findProduct(productId))
                .setProductLongDesc(productLongDesc);
    }
    public void updateStatus(UUID productId, RowStatus rowStatus) {
        Objects.requireNonNull(rowStatus, "El nuevo estado no puede ser nulo.");

        ifProductExists(productRepository.findProduct(productId))
                .setRowStatus(rowStatus);
    }
}
