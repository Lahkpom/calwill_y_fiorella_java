package com.calwillyfiorella.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class SaleItem {
    UUID            saleItemId;
    Sale            sale;
    ProductVariant  productVariant;
    String          variantDesc;
    String          productName;
    String          saleItemSku;
    BigDecimal      saleItemUnitPrice;
    Integer         saleItemQuantity;
    BigDecimal      saleItemSubtotal;
    LocalDateTime   createdAt;

    public SaleItem() {}

    public SaleItem(
            UUID            saleItemId,
            Sale            sale,
            ProductVariant  productVariant,
            String          variantDesc,
            String          productName,
            String          saleItemSku,
            BigDecimal      saleItemUnitPrice,
            Integer         saleItemQuantity,
            BigDecimal      saleItemSubtotal,
            LocalDateTime   createdAt
    ) {
        this.saleItemId         = saleItemId;
        this.sale               = sale;
        this.productVariant     = productVariant;
        this.variantDesc        = variantDesc;
        this.productName        = productName;
        this.saleItemSku        = saleItemSku;
        this.saleItemUnitPrice  = saleItemUnitPrice;
        this.saleItemQuantity   = saleItemQuantity;
        this.saleItemSubtotal   = saleItemSubtotal;
        this.createdAt          = createdAt;
    }

    @Override
    public String toString() {
        return String.format(
                "{ Producto: %s, Variante: %s, Precio Unitario: %s, Cantidad: %d, Subtotal: %s }",
                this.productName,
                this.variantDesc,
                this.saleItemUnitPrice,
                this.saleItemQuantity,
                this.saleItemSubtotal
        );
    }

    public UUID             getId               () { return saleItemId; }
    public Sale             getSale             () { return sale; }
    public ProductVariant   getProductVariant   () { return productVariant; }
    public String           getVariantDesc      () { return variantDesc; }
    public String           getProductName      () { return productName; }
    public String           getSaleItemSku      () { return saleItemSku; }
    public BigDecimal       getSaleItemUnitPrice() { return saleItemUnitPrice; }
    public Integer          getSaleItemQuantity () { return saleItemQuantity; }
    public BigDecimal       getSaleItemSubtotal () { return saleItemSubtotal; }
    public LocalDateTime    getCreatedAt        () { return createdAt; }

    public void setId(UUID saleItemId) { this.saleItemId = saleItemId; }
    public void setSale(Sale sale) { this.sale = sale; }
    public void setProductVariant(ProductVariant productVariant) { this.productVariant = productVariant; }
    public void setVariantDesc(String variantDesc) { this.variantDesc = variantDesc; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setSaleItemSku(String saleItemSku) { this.saleItemSku = saleItemSku; }
    public void setSaleItemUnitPrice(BigDecimal saleItemUnitPrice) { this.saleItemUnitPrice = saleItemUnitPrice; }
    public void setSaleItemQuantity(Integer saleItemQuantity) { this.saleItemQuantity = saleItemQuantity; }
    public void setSaleItemSubtotal(BigDecimal saleItemSubtotal) { this.saleItemSubtotal = saleItemSubtotal; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}