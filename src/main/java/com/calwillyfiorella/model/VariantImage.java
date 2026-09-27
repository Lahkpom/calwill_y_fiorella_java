package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.util.ValidationUtils;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Objects;

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

        if (imageOrder == null || imageOrder < 1 ) throw new IllegalArgumentException("El número de orden de la imágen no puede ser menor a uno.");

        this.imageId    = Objects.requireNonNull(imageId, "imageId cannot be null");
        this.variant    = Objects.requireNonNull(variant, "Variant cannot be null");
        this.imageUrl   = isValidUrl(imageUrl) ? imageUrl : null;
        this.imageOrder = this.validateImageOrder(imageOrder);
    }

    @Override
    public String toString() {
        return String.format(
                "Producto: %s - Categoría: %s - Para: %s - Color: %s - Talle: %s - Precio: %s - URL Imagen: %s - Orden Imagen: %d - Estado: %s",
                this.variant.getProduct().getProductName(),
                this.variant.getProduct().getProductCategory(),
                ProductVariant.targetGenderDecode(this.variant.getTargetGender()),
                this.variant.getColor().getColorName(),
                this.variant.getSize(),
                this.variant.getVariantPrice(),
                this.imageUrl,
                this.imageOrder,
                this.rowStatus
        );
    }

    private boolean isValidUrl(String url) {
        if (url == null || url.isBlank()) return false;
        try {
            URI parsed = URI.create(url);
            String scheme = parsed.getScheme();
            return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private Integer validateImageOrder(Integer imageOrder) {
        return ValidationUtils.requireNonNegative(imageOrder, "imageOrder cannot be null or minus than zero.");
    }

    public void setImageOrder(Integer imageOrder) {
        this.imageOrder = this.validateImageOrder(imageOrder);
        this.afterUpdate();
    }

    public UUID             getImageId      () { return this.imageId; }
    public ProductVariant   getVariant      () { return this.variant; }
    public String           getImageUrl     () { return this.imageUrl; }
    public Integer          getImageOrder   () { return this.imageOrder; }
}