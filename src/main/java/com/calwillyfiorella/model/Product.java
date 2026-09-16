package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.Category;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.TargetGender;
import com.calwillyfiorella.util.ValidationUtils;

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
        this.productId        = Objects.requireNonNull(productId, "Product id cannot be null");
        this.productCategory  = this.validateCategory(productCategory);
        this.productName      = this.validateProductName(productName);
        this.productShortDesc = this.validateProductShortDesc(productShortDesc);
        this.productLongDesc  = this.validateProductLongDesc(productLongDesc);
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
    public List<ProductVariant> getVariants                 () { return Collections.unmodifiableList(this.variants); }
    public Integer              getTotalVariantsAmount      () { return this.variants.size(); }
    public int                  getAvailableVariantsAmount  () { return (int) this.variants.stream().filter(variant -> variant.getRowStatus() == RowStatus.ACTIVE).count(); }
    public UUID                 getProductId                () { return this.productId; }
    public Category             getProductCategory          () { return this.productCategory; }
    public String               getProductName              () { return this.productName; }
    public String               getProductShortDesc         () { return this.productShortDesc; }
    public String               getProductLongDesc          () { return this.productLongDesc; }

    // Agregar objetos a la lista. No se hace Update ya que los cambio no son del Product en si mismo
    public void addVariant(ProductVariant variant) {
        if (variant == null) throw new NullPointerException("Variant cannot be null");

        // Cuando se de este caso hay que ver de preguntarle al usuario si es que quiere cambiarle el Producto a la Variante
        if (!variant.getProduct().getProductId().equals(this.productId)) throw new IllegalArgumentException("La producVariant ingresada no corresponde a una variante de este producto");

        if (this.variants.stream().anyMatch(v -> variant.getVariantId().equals(v.getVariantId()))) throw new IllegalArgumentException("La vairante ya se encuentra ingresada en la lista de variantes de este producto");

        this.variants.add(variant);
    }

    public ProductVariant addVariant(
            Color           color,
            Size            size,
            TargetGender    targetGender,
            String          variantDesc,
            String          variantSku,
            BigDecimal      variantPrice,
            Integer         variantStock
    ) {
        if (variantSku == null || this.variants.stream().anyMatch(v -> v.getVariantSku().equals(variantSku))) throw new IllegalArgumentException("Ya existe en la lista de variantes una variante con la misma SKU.");

        ProductVariant pv = new ProductVariant(this, color, size, targetGender, variantDesc, variantSku, variantPrice, variantStock);

        this.variants.add(pv);

        return pv;
    }

    public void setProductCategory(Category productCategory) {
        this.productCategory = this.validateCategory(productCategory);
        this.afterUpdate();
    }

    public void setProductName(String productName) {
        this.productName = this.validateProductName(productName);
        this.afterUpdate();
    }

    public void setProductShortDesc(String productShortDesc) {
        this.productShortDesc = this.validateProductShortDesc(productShortDesc);
        this.afterUpdate();
    }

    public void setProductLongDesc(String productLongDesc) {
        this.productLongDesc = this.validateProductLongDesc(productLongDesc);
        this.afterUpdate();
    }

    private Category validateCategory(Category productCategory) {
        return Objects.requireNonNull(productCategory, "Product category cannot be null");
    }

    private String validateProductName(String productName) {
        return ValidationUtils.requireNonBlank(productName, "Product name cannot be blank");
    }

    private String validateProductShortDesc(String productShortDesc) {
        return ValidationUtils.requireNonBlank(productShortDesc, "Product short desc cannot be blank");
    }

    private String validateProductLongDesc(String productLongDesc) {
        return ValidationUtils.requireNonBlank(productLongDesc, "Product long desc cannot be blank");
    }
}