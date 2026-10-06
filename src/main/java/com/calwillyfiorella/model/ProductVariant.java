package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.NumericSize;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.TargetGender;
import com.calwillyfiorella.service.ProductVariantService;

import java.time.LocalDateTime;
import java.util.*;
import java.math.BigDecimal;

public class ProductVariant extends BaseEntity {
    private final List<VariantImage>  images = new ArrayList<>();

    private UUID            variantId;
    private Product         product;
    private Color           color;
    private NumericSize     size;
    private TargetGender    targetGender;
    private String          variantDesc;
    private String          variantSku;
    private BigDecimal      variantPrice;
    private Integer         variantStock;

    public ProductVariant() {
        super();
    }

    public ProductVariant(
            Product         product,
            Color           color,
            NumericSize size,
            TargetGender    targetGender,
            String          variantDesc,
            String          variantSku,
            BigDecimal      variantPrice,
            Integer         variantStock
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
            NumericSize     size,
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
                "{ Producto: %s, Categoría: %s, Para: %s, Color: %s, Talle: %s, Precio: %.2f, Stock: %d, Imágenes: %d, Estado: %s }",
                this.product.getName(),
                this.product.getCategory(),
                ProductVariantService.targetGenderDecode(this.targetGender),
                this.color,
                this.size,
                this.variantPrice,
                this.variantStock,
                this.images.stream().filter(img -> img.getRowStatus() == RowStatus.ACTIVE).count(),
                this.rowStatus
        );
    }

    public String toStringComplete() {
        return """
            {
                ID          : (%s).
                Producto    : %s.
                Color       : %s.
                Talle       : %s.
                Para        : %s.
                Descripción : %s.
                SKU         : %s.
                Stock       : %d.
                Precio      : %s.
                Imágenes    : %d.
                Estado      : %s.
                Fecha Inicio: %td/%<tm/%<tY (%<tT).
                Última Mod  : %S.
            }""".formatted(
                this.variantId,
                this.product,
                this.color,
                this.size,
                ProductVariantService.targetGenderDecode(this.targetGender),
                this.variantDesc,
                this.variantSku,
                this.variantStock,
                this.variantPrice,
                this.images.stream().filter(img -> img.getRowStatus() == RowStatus.ACTIVE).count(),
                super.rowStatus,
                super.createdAt,
                (super.updatedAt != null)
                        ? "%td/%<tm/%<tY (%<tT)".formatted(super.updatedAt)
                        : "Sin modificaciones"
        );
    }

//    UTIL FUNCTIONS
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
//    UTIL FUNCTIONS

//    SETTERS
    public void setId(UUID variantId) {
        this.variantId = variantId;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public void setColor(Color color) {
        this.color = color;
        this.afterUpdate();
    }
    public void setSize(NumericSize size) {
        this.size = size;
        this.afterUpdate();
    }
    public void setTargetGender(TargetGender targetGender) {
        this.targetGender = targetGender;
        this.afterUpdate();
    }
    public void setDesc(String variantDesc) {
        this.variantDesc = variantDesc;
        this.afterUpdate();
    }
    public void setSku(String variantSku) {
        this.variantSku = variantSku;
        this.afterUpdate();
    }
    public void setPrice(BigDecimal variantPrice) {
        this.variantPrice = variantPrice;
        this.afterUpdate();
    }
    public void setStock(Integer variantStock) {
        this.variantStock = variantStock;
        this.afterUpdate();
    }
//    SETTERS

//    GETTERS
    public UUID                 getId           () { return this.variantId; }
    public Product              getProduct      () { return this.product; }
    public List<VariantImage>   getImages       () { return Collections.unmodifiableList(this.images); }
    public Color                getColor        () { return this.color; }
    public NumericSize          getSize         () { return this.size; }
    public TargetGender         getTargetGender () { return this.targetGender; }
    public String               getDesc         () { return this.variantDesc; }
    public String               getSku          () { return this.variantSku; }
    public BigDecimal           getPrice        () { return this.variantPrice; }
    public Integer              getStock        () { return this.variantStock; }
//    GETTERS

//    COMPLEX FUNCTIONS
    public void addImage(VariantImage image) {
        if (image == null)
            throw new NullPointerException("La VariantImage ingresada no puede ser nulla.");

        if (!image.getVariant().getId().equals(this.variantId))
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

    public VariantImage addImage(String imageUrl) {
        if (this.images.stream().anyMatch(img -> img.getImageUrl().equalsIgnoreCase(imageUrl)))
            throw new IllegalArgumentException("La imagen ingresada ya existe en la lista de esta variante.");

        VariantImage vi = new VariantImage(this, imageUrl, this.images.size() + 1);

        this.images.add(vi);

        return vi;
    }

    public boolean removeImage(UUID imageId) {
        return this.images.removeIf(img -> imageId.equals(img.getImageId()));
    }

    public void changeImageStatus(UUID imageId, RowStatus newStatus) {
        VariantImage image = this.getImageById(imageId);

        if (image == null)
            throw new IllegalArgumentException("La URL ingresada no corresponde a una imagen de esta variante.");

        if (image.getRowStatus() == newStatus) return;

        image.setRowStatus(newStatus);
    }
//    COMPLEX FUNCTIONS
}