package com.calwillyfiorella.repository;

import com.calwillyfiorella.model.ProductVariant;

import java.util.*;

public class ProductVariantRepository {
    private final List<ProductVariant> variants;

    public ProductVariantRepository() { variants = new ArrayList<>(); }

    // ProductVariantRepository
    public void saveVariant(ProductVariant variant) { this.variants.add(variant); }

    public List<ProductVariant> findAllVariants() { return Collections.unmodifiableList(this.variants); }

    public List<ProductVariant> findAllVariantsOf(UUID productId) {
        return this.variants.stream()
                .filter(v -> v.getProduct().getId().equals(productId))
                .toList();
    }

    public Optional<ProductVariant> findVariant(UUID variantId) {
        return this.variants.stream()
                .filter(v -> v.getId().equals(variantId))
                .findFirst();
    }
    public Optional<ProductVariant> findVariant(String variantSKU) {
        return this.variants.stream()
                .filter(v -> v.getSku().equalsIgnoreCase(variantSKU))
                .findFirst();
    }

    public void delete(ProductVariant variant) { this.variants.remove(variant); }

}
