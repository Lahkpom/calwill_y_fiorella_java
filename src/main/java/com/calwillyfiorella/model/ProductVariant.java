package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.TargetGender;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ProductVariant extends BaseEntity {
    private final UUID                variantId;
    private final Product             product;
    private final List<VariantImage>  images = new ArrayList<>();

    private Color           color;
    private Size            size;
    private TargetGender    targetGender;
    private String          variantDesc;
    private String          variantSku;
    private BigDecimal      variantPrice;
    private Integer         variantStock;

    public ProductVariant(
            Product             product,
            Color               color,
            Size                size,
            TargetGender        targetGender,
            String              variantDesc,
            String              variantSku,
            BigDecimal          variantPrice,
            Integer             variantStock
    ) {
        this(
                UUID.randomUUID(),
                product,
                color,
                size,
                targetGender,
                variantDesc,
                variantSku,
                variantPrice,
                variantStock,
                RowStatus.ACTIVE,
                LocalDateTime.now(),
                null
        );
    }

    public ProductVariant(
            UUID            variantId,
            Product         product,
            Color           color,
            Size            size,
            TargetGender    targetGender,
            String          variantDesc,
            String          variantSku,
            BigDecimal      variantPrice,
            Integer         variantStock,
            RowStatus       rowStatus,
            LocalDateTime   createdAt,
            LocalDateTime   updatedAt
    ) {
        super(rowStatus, createdAt, updatedAt);
        this.variantId      = variantId;
        this.product        = product;
        this.color          = color;
        this.size           = size;
        this.targetGender   = targetGender;
        this.variantDesc    = variantDesc;
        this.variantSku     = variantSku;
        this.variantPrice   = variantPrice;
        this.variantStock   = variantStock;
    }

    @Override
    public String toString() {
        return String.format(
                "Producto: %s - Categoría: %s - Para: %s - Color: %s - Talle: %s - Precio: %s - Imágenes: %d - Estado: %s",
                this.product.getProductName(),
                this.product.getProductCategory(),
                targetGenderDecode(this.targetGender),
                this.color.getColorName(),
                this.size.getSize(),
                this.variantPrice,
                this.images.size(),
                this.rowStatus
        );
    }

    public static String targetGenderDecode(TargetGender tg) {
        return switch(tg) {
            case NINIAS     -> "Niñas";
            case NINIOS     -> "Niños";
            case HOMBRES    -> "Hombres";
            case MUJERES    -> "Mujeres";
            case UNISEX     -> "Unisex";
        };
    }

    public void addImage(VariantImage image) {
        if (image != null) this.images.add(image);
    }

    public void setColor(Color color) {
        this.color = color;
        this.afterUpdate();
    }

    public void setSize(Size size) {
        this.size = size;
        this.afterUpdate();
    }

    public void setTargetGender(TargetGender targetGender) {
        this.targetGender = targetGender;
        this.afterUpdate();
    }

    public void setVariantDesc(String variantDesc) {
        this.variantDesc = variantDesc;
        this.afterUpdate();
    }

    public void setVariantSku(String variantSku) {
        this.variantSku = variantSku;
        this.afterUpdate();
    }

    public void setVariantPrice(BigDecimal variantPrice) {
        this.variantPrice = variantPrice;
        this.afterUpdate();
    }

    public void setVariantStock(Integer variantStock) {
        this.variantStock = variantStock;
        this.afterUpdate();
    }

    public UUID                 getVariantId    () { return this.variantId; }
    public Product              getProduct      () { return this.product; }
    public List<VariantImage>   getImages       () { return Collections.unmodifiableList(this.images); }
    public Color                getColor        () { return this.color; }
    public Size                 getSize         () { return this.size; }
    public TargetGender         getTargetGender () { return this.targetGender; }
    public String               getVariantDesc  () { return this.variantDesc; }
    public String               getVariantSku   () { return this.variantSku; }
    public BigDecimal           getVariantPrice () { return this.variantPrice; }
    public Integer              getVariantStock () { return this.variantStock; }
}