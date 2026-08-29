package com.calwillyfiorella;

import com.calwillyfiorella.model.enums.RowStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Product {
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(name = "product_id", updatable = false, nullable = false)
    private UUID productId;
//    @JoinColumn(name = "category_id", nullable = false)
    private Integer categoryId;
//    @Column(name = "product_name", nullable = false, unique = true, length = 255)
    private String productName;
//    @Column(name = "product_short_desc", nullable = false, length = 255)
    private String productShortDesc;
//    @Column(name = "product_long_desc", nullable = false, columnDefinition = "TEXT")
    private String productLongDesc;
//    @Column(name = "product_base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal productBasePrice;
//    @Enumerated(EnumType.STRING)
//    @Column(name = "row_status", nullable = false)
    private RowStatus rowStatus;
//    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
//    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Product(
            UUID        productId,
            Integer     categoryId,
            String      productName,
            String      productShortDesc,
            String      productLongDesc,
            BigDecimal  productBasePrice,
            RowStatus   rowStatus
    ) {
        this.productId          = productId;
        this.categoryId         = categoryId;
        this.productName        = productName;
        this.productShortDesc   = productShortDesc;
        this.productLongDesc    = productLongDesc;
        this.productBasePrice   = productBasePrice;
        this.rowStatus          = rowStatus;
    }

//    // Callbacks de ciclo de vida para auditoría automática
//    @PrePersist
    protected void onCreate() {
        this.rowStatus = RowStatus.active;
        this.createdAt = LocalDateTime.now();
    }

//    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public UUID getProductId() { return productId; }

    public void setProductId(UUID productId) { this.productId = productId; }

    public Integer getCategory() { return categoryId; }

    public void setCategory(Integer categoryId) { this.categoryId = categoryId; }

    public String getProductName() { return productName; }

    public void setProductName(String productName) { this.productName = productName; }

    public String getProductShortDesc() { return productShortDesc; }

    public void setProductShortDesc(String productShortDesc) { this.productShortDesc = productShortDesc; }

    public String getProductLongDesc() { return productLongDesc; }

    public void setProductLongDesc(String productLongDesc) { this.productLongDesc = productLongDesc; }

    public BigDecimal getProductBasePrice() { return productBasePrice; }

    public void setProductBasePrice(BigDecimal productBasePrice) { this.productBasePrice = productBasePrice; }

    public RowStatus getRowStatus() { return rowStatus; }

    public void setRowStatus(RowStatus rowStatus) { this.rowStatus = rowStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

}
