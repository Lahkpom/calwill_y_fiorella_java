package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.RowStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class VariantImage extends BaseEntity{
    private final UUID              imageId;
    private final ProductVariant    variant;
    private final String            imageUrl;

    private Integer imageOrder;

    public VariantImage(
            ProductVariant variant,
            String imageUrl,
            Integer imageOrder
    ) {
        this(
                UUID.randomUUID(),
                variant,
                imageUrl,
                imageOrder,
                RowStatus.ACTIVE,
                LocalDateTime.now(),
                null
        );
    }

    public VariantImage(
            UUID            imageId,
            ProductVariant  variant,
            String          imageUrl,
            Integer         imageOrder,
            RowStatus       rowStatus,
            LocalDateTime   createdAt,
            LocalDateTime   updatedAt
    ) {
        super(rowStatus, createdAt, updatedAt);
        this.imageId    = imageId;
        this.variant    = variant;
        this.imageUrl   = imageUrl;
        this.imageOrder = imageOrder;
    }

    @Override
    public String toString() {
        return String.format(
                "Producto: %s - Categoría: %s - Para: %s - Color: %s - Talle: %s - Precio: %s - URL Imagen: %d - Orden Imagen: %d - Estado: %s",
                this.variant.getProduct().getProductName(),
                this.variant.getProduct().getProductCategory(),
                ProductVariant.targetGenderDecode(this.variant.getTargetGender()),
                this.variant.getColor().getColorName(),
                this.variant.getSize().getSize(),
                this.variant.getVariantPrice(),
                this.rowStatus
        );
    }

    public void setImageOrder(Integer imageOrder) {
        this.imageOrder = imageOrder;
        afterUpdate();
    }

    public UUID getImageId() { return imageId; }
    public ProductVariant getVariant() { return variant; }
    public String getImageUrl() { return imageUrl; }
    public Integer getImageOrder() { return imageOrder; }
}