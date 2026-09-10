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

    public boolean addImage(VariantImage image) {
        if (image == null) return false;

        if (!image.getVariant().getVariantId().equals(this.variantId)) return false;

        if (this.images.stream().anyMatch(img ->
                (
                        image.getImageId() != null && image.getImageId().equals(img.getImageId())
                ) || (
                        image.getImageUrl() != null && image.getImageUrl().equalsIgnoreCase(img.getImageUrl())
                )
        )) return false;

        this.images.add(image);
        return true;
    }

    public boolean removeImage(UUID imageId) {
        if (imageId == null) return false;
        return this.images.removeIf(img -> imageId.equals(img.getImageId()));
    }

    private VariantImage getImageById(UUID imageId) {
        VariantImage dummy = null;
        for (VariantImage img : this.images) {
            if (img.getImageId().equals(imageId)) {
                dummy = img;
                break;
            }
        }
        return dummy;
    }

    public boolean changeImageStatus(UUID imageId, RowStatus newStatus) {
        if (imageId == null || newStatus == null) return false;

        VariantImage image = this.getImageById(imageId);

        if (image == null) return false;

        if (image.getRowStatus() == newStatus) return false;

        switch (newStatus) {
            case ACTIVE     -> image.activate();
            case INACTIVE   -> image.deactive();
            case DELETED    -> image.delete();
        }

        return true;
    }

    public boolean incrementStock (Integer stock) {
        if (stock == null || stock <= 0) return false;
        this.variantStock += stock;
        this.afterUpdate();
        return true;
    }

    public boolean decrementStock (Integer stock) {
        if (stock == null || this.variantStock - stock < 0) return false;
        this.variantStock -= stock;
        this.afterUpdate();
        return true;
    }

    public boolean setColor(Color color) {
        if (color == null) return false;
        this.color = color;
        this.afterUpdate();
        return true;
    }

    public boolean setSize(Size size) {
        if (size == null) return false;
        this.size = size;
        this.afterUpdate();
        return true;
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