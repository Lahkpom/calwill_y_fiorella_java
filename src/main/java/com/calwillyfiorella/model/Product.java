package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.Category;
import com.calwillyfiorella.model.enums.RowStatus;

import java.time.LocalDateTime;
import java.util.*;

public class Product extends BaseEntity{
    private final UUID                  productId;
    private final List<ProductVariant>  variants = new ArrayList<>();

    private Category        productCategory;
    private String          productName;
    private String          productShortDesc;
    private String          productLongDesc;

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
                "Producto: %s - Categoría: %s - Variantes: %d - Estado: %s",
                this.productName,
                this.productCategory,
                this.variants.size(),
                this.rowStatus
        );
    }

    // Getters y Setters
    // Devuelve copia inmodificable
    public List<ProductVariant> getVariants         () { return Collections.unmodifiableList(variants); }
    public UUID                 getProductId        () { return productId; }
    public Category             getProductCategory  () { return productCategory; }
    public String               getProductName      () { return productName; }
    public String               getProductShortDesc () { return productShortDesc; }
    public String               getProductLongDesc  () { return productLongDesc; }

    // Agregar objetos a la lista. No se hace Update ya que los cambio no son del Product en si mismo
    public void addVariant(ProductVariant variant) {
        if (variant != null) this.variants.add(variant);
    }

    public void setProductCategory(Category productCategory) {
        this.productCategory = productCategory;
        afterUpdate();
    }

    public void setProductName(String productName) {
        this.productName = productName;
        afterUpdate();
    }

    public void setProductShortDesc(String productShortDesc) {
        this.productShortDesc = productShortDesc;
        afterUpdate();
    }

    public void setProductLongDesc(String productLongDesc) {
        this.productLongDesc = productLongDesc;
        afterUpdate();
    }
}