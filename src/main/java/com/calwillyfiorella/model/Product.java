package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.Category;
import com.calwillyfiorella.model.enums.NumericSize;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.TargetGender;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class Product extends BaseEntity{
    private final List<ProductVariant>  variants = new ArrayList<>();

    private UUID        productId;
    private Category    productCategory;
    private String      productName;
    private String      productShortDesc;
    private String      productLongDesc;

    public Product() {}

    // Constructor para NUEVOS productos
    public Product(
            Category    productCategory,
            String      productName,
            String      productShortDesc,
            String      productLongDesc
    ) {
        this(
            UUID.randomUUID(),
            productCategory,
            productName,
            productShortDesc,
            productLongDesc,
            RowStatus.ACTIVE,
            LocalDateTime.now(),
            null
        );
    }

    // Constructor para reconstruir productos DESDE LA DB
    public Product(
            UUID            productId,
            Category        productCategory,
            String          productName,
            String          productShortDesc,
            String          productLongDesc,
            RowStatus       rowStatus,
            LocalDateTime   createdAt,
            LocalDateTime   updatedAt
    ) {
        super(rowStatus, createdAt, updatedAt);
        this.productId        = productId;
        this.productCategory  = productCategory;
        this.productName      = productName;
        this.productShortDesc = productShortDesc;
        this.productLongDesc  = productLongDesc;
    }

    @Override
    public String toString() {
        return String.format(
                "{ Nombre: %s, Categoría: %s, Variantes: %d, Estado: %s }",
                this.productName,
                this.productCategory,
                this.variants.stream().filter(v -> v.getRowStatus() == RowStatus.ACTIVE).count(),
                this.rowStatus
        );
    }

    // Getters y Setters
    public UUID     getId       () { return this.productId; }
    public Category getCategory () { return this.productCategory; }
    public String   getName     () { return this.productName; }
    public String   getShortDesc() { return this.productShortDesc; }
    public String   getLongDesc () { return this.productLongDesc; }

    public void setId(UUID id) {
        this.productId = id;
        this.afterUpdate();
    }
    public void setCategory(Category productCategory) {
        this.productCategory = productCategory;
        this.afterUpdate();
    }
    public void setName(String productName) {
        this.productName = productName;
        this.afterUpdate();
    }
    public void setShortDesc(String productShortDesc) {
        this.productShortDesc = productShortDesc;
        this.afterUpdate();
    }
    public void setLongDesc(String productLongDesc) {
        this.productLongDesc = productLongDesc;
        this.afterUpdate();
    }

    // ProductVariantRepository
    public void saveVariant(ProductVariant variant) { this.variants.add(variant); }

    public List<ProductVariant> findAllVariants() { return Collections.unmodifiableList(this.variants); }

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