package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.TargetGender;
import java.time.LocalDateTime;
import java.util.*;
import java.math.BigDecimal;
import java.net.URI;

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
        this.variantPrice   = variantPrice != null ? variantPrice : BigDecimal.ZERO;
        this.variantStock   = variantStock != null ? variantStock : 0;
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
        if (image == null)
            throw new NullPointerException("La VariantImage ingresada no puede ser nulla.");

        if (!image.getVariant().getVariantId().equals(this.variantId))
            throw new IllegalArgumentException("La VariantImage ingresada no corresponde a esta variante.");

        if (this.images.stream().anyMatch(img ->
                (
                        image.getImageId().equals(img.getImageId())
                ) || (
                        image.getImageUrl().equalsIgnoreCase(img.getImageUrl())
                )
        )) throw new IllegalArgumentException("La VariantImage ingresada ya se encuentra en la Lista de esta variante");

        this.images.add(image);
    }

    public void addImage(String imageUrl) {
        if (this.images.stream().anyMatch(img -> img.getImageUrl().equalsIgnoreCase(imageUrl)))
            throw new IllegalArgumentException("La imagen ingresada ya existe en la lista de esta variante.");

        this.images.add(new VariantImage(this, imageUrl, this.images.size() + 1));
    }

    public boolean removeImage(UUID imageId) {
        return this.images.removeIf(img -> imageId.equals(img.getImageId()));
    }

    private VariantImage getImageById(UUID imageId) {
        if (imageId == null) return null;

        VariantImage dummy = null;
        for (VariantImage img : this.images) {
            if (img.getImageId().equals(imageId)) {
                dummy = img;
                break;
            }
        }
        return dummy;
    }

    public void changeImageStatus(UUID imageId, RowStatus newStatus) {
        VariantImage image = this.getImageById(imageId);

        if (image == null)
            throw new IllegalArgumentException("La URL ingresada no corresponde a una imagen de esta variante.");

        if (image.getRowStatus() == newStatus) return;

        switch (newStatus) {
            case ACTIVE     -> image.activate();
            case INACTIVE   -> image.deactive();
            case DELETED    -> image.delete();
        }
    }

    public void incrementStock (Integer stock) {
        if (stock == null || stock <= 0)
            throw new IllegalArgumentException("La cantidad ingresada para incrementar el stock no puede ser nula ni menor o igual a cero.");

        this.variantStock += stock;
        this.afterUpdate();
    }

    public void decrementStock (Integer stock) {
        if (stock == null || this.variantStock - stock < 0)
            throw new IllegalArgumentException("La cantidad ingresada para disminuir el stock no puede ser nula ni hacer que el resultado final deje a stock menor a cero.");

        this.variantStock -= stock;
        this.afterUpdate();
    }

    public void setColor(Color color) {
        this.color = Objects.requireNonNull(color, "color cannot be null");
        this.afterUpdate();
    }

    public boolean setSize(Size size) {
        this.size = Objects.requireNonNull(size, "size cannot be null");
        this.afterUpdate();
        return true;
    }

    public void setTargetGender(TargetGender targetGender) {
        this.targetGender = Objects.requireNonNull(targetGender, "targetGender cannot be null");
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

    public boolean setVariantPrice(BigDecimal variantPrice) {
        if (variantPrice == null || variantPrice.compareTo(BigDecimal.ZERO) == 0) return false;
        this.variantPrice = variantPrice;
        this.afterUpdate();
        return true;
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