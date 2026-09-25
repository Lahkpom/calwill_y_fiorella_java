package com.calwillyfiorella.service;

import com.calwillyfiorella.exception.ProductDoesNotExistException;
import com.calwillyfiorella.model.Product;
import com.calwillyfiorella.model.enums.Category;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.repository.ProductRepository;

import java.util.List;

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
        if (this.productRepository.findByName(productName).isPresent())
            throw new IllegalArgumentException("Product already exists!");
    }

    public Product getProductByName(String productName) {
        return this.productRepository.findByName(productName).orElseThrow(ProductDoesNotExistException::new);
    }

    public List<Product> getAllProducts() { return this.productRepository.findAll(); }

    public void updateCategory(Product product, Category category) {
        product.setProductCategory(category);
    }
    public void updateName(Product product, String productName) {
        product.setProductName(productName);
    }
    public void updateShortDesc(Product product, String productShortDesc) {
        product.setProductShortDesc(productShortDesc);
    }
    public void updateLongDesc(Product product, String productLongDesc) {
        product.setProductLongDesc(productLongDesc);
    }
    public void updateStatus(Product product, RowStatus rowStatus) {
        product.setRowStatus(rowStatus);
    }

}
