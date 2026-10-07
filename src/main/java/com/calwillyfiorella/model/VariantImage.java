package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.service.ProductVariantService;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Objects;

public class VariantImage extends BaseEntity{
    private UUID            imageId;
    private ProductVariant  variant;
    private String          imageUrl;
    private Integer         imageOrder;

    public VariantImage() {}

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
        this.imageUrl   = imageUrl;
        this.imageOrder = imageOrder;
    }

    @Override
    public String toString() {
        return "{ Orden Imagen: %d, Estado: %s, URL Imagen: %s }"
                .formatted(
                        this.imageOrder,
                        super.rowStatus,
                        this.imageUrl
                );
    }

    public String toStringComplete() {
        return """
                {
                    ID          : (%s).
                    Producto    : %s.
                    Variante    : %s.
                    Imágen Nro  : %d.
                    URL         : %s.
                    Estado      : %s.
                    Fecha Inicio: %td/%<tm/%<tY (%<tT).
                    Última Mod  : %S.
                }""".formatted(
                        this.imageId,
                        this.variant.getProduct(),
                        this.variant,
                        this.imageOrder,
                        this.imageUrl,
                        super.rowStatus,
                        super.createdAt,
                        (super.updatedAt != null)
                                ? "%td/%<tm/%<tY (%<tT)".formatted(super.updatedAt)
                                : "Sin modificaciones"
        );
    }

    public void setId(UUID imageId) {
        this.imageId = imageId;
    }
    public void setVariant(ProductVariant variant) {
        this.variant = variant;
    }
    public void setUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.afterUpdate();
    }
    public void setOrder(Integer imageOrder) {
        this.imageOrder = imageOrder;
        this.afterUpdate();
    }

    public UUID             getId       () { return this.imageId; }
    public ProductVariant   getVariant  () { return this.variant; }
    public String           getUrl      () { return this.imageUrl; }
    public Integer          getOrder    () { return this.imageOrder; }
}