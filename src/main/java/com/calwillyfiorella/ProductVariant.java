package com.calwillyfiorella;

import com.calwillyfiorella.model.enums.RowStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProductVariant {
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(name = "variant_id", updatable = false, nullable = false)
    private UUID variantId;
//    @JoinColumn(name = "product_id", nullable = false)
    private UUID productId;
//    @JoinColumn(name = "color_id", nullable = false)
    private Integer  colorId;
//    @JoinColumn(name = "size_id", nullable = false)
    private Integer  sizeId;
//    @Column(name = "variant_sku", nullable = false, length = 255)
    private String  variantSku;
//    @Column(name = "variant_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal  variantPrice;
//    @Enumerated(EnumType.STRING)
//    @Column(name = "row_status", nullable = false)
    private RowStatus  rowStatus;
//    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
//    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ProductVariant(
            UUID            variantId,
            UUID            productId,
            Integer         colorId,
            Integer         sizeId,
            String          variantSku,
            BigDecimal      variantPrice,
            RowStatus       rowStatus,
            LocalDateTime   createdAt,
            LocalDateTime   updatedAt
    ) {
        this.variantId      = variantId;
        this.productId      = productId;
        this.colorId        = colorId;
        this.sizeId         = sizeId;
        this.variantSku     = variantSku;
        this.variantPrice   = variantPrice;
        this.rowStatus      = rowStatus;
        this.createdAt      = createdAt;
        this.updatedAt      = updatedAt;
    }

// Callbacks de ciclo de vida para auditoría automática
//    @PrePersist
    protected void onCreate() {
        this.rowStatus = RowStatus.active;
        this.createdAt = LocalDateTime.now();
    }

    //    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getVariantId() {
        return variantId;
    }

    public void setVariantId(UUID variantId) {
        this.variantId = variantId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getColorId() {
        return colorId;
    }

    public void setColorId(Integer colorId) {
        this.colorId = colorId;
    }

    public Integer getSizeId() {
        return sizeId;
    }

    public void setSizeId(Integer sizeId) {
        this.sizeId = sizeId;
    }

    public String getVariantSku() {
        return variantSku;
    }

    public void setVariantSku(String variantSku) {
        this.variantSku = variantSku;
    }

    public BigDecimal getVariantPrice() {
        return variantPrice;
    }

    public void setVariantPrice(BigDecimal variantPrice) {
        this.variantPrice = variantPrice;
    }

    public RowStatus getRowStatus() {
        return rowStatus;
    }

    public void setRowStatus(RowStatus rowStatus) {
        this.rowStatus = rowStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
