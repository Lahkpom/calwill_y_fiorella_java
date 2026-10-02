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
    public List<ProductVariant> getVariants () { return Collections.unmodifiableList(this.variants); }

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

    // Agregar objetos a la lista. No se hace Update, ya que los cambios no son del Product en sí mismo
    public void addVariant(ProductVariant variant) {
        if (variant == null) throw new NullPointerException("Variant cannot be null");

        // Cuando se de este caso hay que ver de preguntarle al usuario si es que quiere cambiarle el Producto a la Variante
        if (!variant.getProduct().getId().equals(this.productId)) throw new IllegalArgumentException("La producVariant ingresada no corresponde a una variante de este producto");

        if (this.variants.stream().anyMatch(v -> variant.getVariantId().equals(v.getVariantId()))) throw new IllegalArgumentException("La vairante ya se encuentra ingresada en la lista de variantes de este producto");

        this.variants.add(variant);
    }

    public ProductVariant addVariant(
            Color           color,
            NumericSize size,
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

    public ProductVariant updateVariant(UUID variantId, ProductVariant newVariant) {
        ProductVariant currentVariant = this.variants.stream().filter(v -> v.getVariantId().equals(variantId)).findFirst().orElseThrow(() -> new IllegalStateException("Variante no encontrado"));

        currentVariant.setColor(newVariant.getColor());
        currentVariant.setSize(newVariant.getSize());
        currentVariant.setTargetGender(newVariant.getTargetGender());
        currentVariant.setVariantDesc(newVariant.getVariantDesc());
        currentVariant.setVariantSku(newVariant.getVariantSku());
        currentVariant.setVariantPrice(newVariant.getVariantPrice());
        currentVariant.setVariantStock(newVariant.getVariantStock());
        currentVariant.setRowStatus(newVariant.getRowStatus());

        return currentVariant;
    }




}