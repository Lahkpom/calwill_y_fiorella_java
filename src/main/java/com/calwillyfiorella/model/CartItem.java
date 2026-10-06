package com.calwillyfiorella.model;

import java.math.BigDecimal;
import java.util.Objects;

import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.util.ValidationUtils;

public class CartItem {
    private final Users             user;
    private final ProductVariant    variant;

    private Integer quantity;

    public CartItem(
            Users           user,
            ProductVariant  variant,
            Integer         quantity
    ) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(variant);

        if (variant.getRowStatus() != RowStatus.ACTIVE)
            throw new IllegalArgumentException("No puede ingresar una variante que no está ACTIVE al carrito de compras!");

        this.user       = user;
        this.variant    = variant;
        this.quantity   = this.validateQuantity(quantity);
    }

    @Override
    public String toString() {
        return String.format("{ Cantidad: %d, Variante: $%s }", this.quantity, this.variant);
    }

    public void setQuantity(Integer quantity) {
        this.quantity = this.validateQuantity(quantity);
    }

    private Integer validateQuantity(Integer quantity) {
        return ValidationUtils.requireNonNegative(quantity, "La cantidad ingresada no puede ser menor a cero", true);
    }

    public Users            getUser     () { return this.user; }
    public ProductVariant   getVariant  () { return this.variant; }
    public Integer          getQuantity () { return this.quantity; }

    public BigDecimal getSubtotal() {
        if (variant == null || variant.getPrice() == null || quantity == null)
            return BigDecimal.ZERO;

        return variant.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
