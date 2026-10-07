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
        return "{ Producto: %s, Categoría: %s, Para: %s, Color: %s, Talle: %s, Precio: %.2f, Stock: %d, Imágenes: %d, Estado: %s }"
                .formatted(
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

    public UUID                 getId           () { return this.variantId; }
    public Product              getProduct      () { return this.product; }
    public Color                getColor        () { return this.color; }
    public NumericSize          getSize         () { return this.size; }
    public TargetGender         getTargetGender () { return this.targetGender; }
    public String               getDesc         () { return this.variantDesc; }
    public String               getSku          () { return this.variantSku; }
    public BigDecimal           getPrice        () { return this.variantPrice; }
    public Integer              getStock        () { return this.variantStock; }

    // VARIANT IMAGES
    public void saveImage(VariantImage variantImage) { this.images.add(variantImage); }

    public List<VariantImage> findAllImages() { return Collections.unmodifiableList(this.images); }

    public Optional<VariantImage> findImageById (UUID imageId) { return this.images.stream().filter(img -> imageId.equals(img.getId())).findFirst(); }

    public void deleteImageById (UUID imageId) { this.images.removeIf(img -> imageId.equals(img.getId())); }

}