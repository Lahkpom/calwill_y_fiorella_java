package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.Category;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.TargetGender;

import java.math.BigDecimal;
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
    public List<ProductVariant> getVariants         () { return Collections.unmodifiableList(this.variants); }
    public UUID                 getProductId        () { return this.productId; }
    public Category             getProductCategory  () { return this.productCategory; }
    public String               getProductName      () { return this.productName; }
    public String               getProductShortDesc () { return this.productShortDesc; }
    public String               getProductLongDesc  () { return this.productLongDesc; }

    // Agregar objetos a la lista. No se hace Update ya que los cambio no son del Product en si mismo
    public boolean addVariant(ProductVariant variant) {
        if (variant == null) return false;

        // Cuando se de este caso hay que ver de preguntarle al usuario si es que quiere cambiarle el Producto a la Variante
        if (variant.getProduct() == null || variant.getProduct().getProductId() == null || !variant.getProduct().getProductId().equals(this.productId)) return false;

        if (this.variants.stream().anyMatch(v -> variant.getVariantId() != null && variant.getVariantId().equals(v.getVariantId()))) return false;

        this.variants.add(variant);
        return true;
    }

    public boolean addVariant(
            Color           color,
            Size            size,
            TargetGender    targetGender,
            String          variantDesc,
            String          variantSku,
            BigDecimal      variantPrice,
            Integer         variantStock
    ) {
        if (variantSku == null || this.variants.stream().anyMatch(v -> v.getVariantSku().equals(variantSku))) return false;

        // Acá va un try-catch
        this.variants.add(new ProductVariant(this, color, size, targetGender, variantDesc, variantSku, variantPrice, variantStock));
        return true;
    }

    public void setProductCategory(Category productCategory) {
        this.productCategory = productCategory;
        this.afterUpdate();
    }

    public void setProductName(String productName) {
        this.productName = productName;
        this.afterUpdate();
    }

    public void setProductShortDesc(String productShortDesc) {
        this.productShortDesc = productShortDesc;
        this.afterUpdate();
    }

    public void setProductLongDesc(String productLongDesc) {
        this.productLongDesc = productLongDesc;
        this.afterUpdate();
    }
}