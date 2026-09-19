package com.calwillyfiorella.model;

import com.calwillyfiorella.util.ValidationUtils;

import java.util.Objects;

public class CartItem {
    private final Users             user;
    private final ProductVariant    variant;

    private Integer quantity;

    public CartItem(
            Users           user,
            ProductVariant  variant,
            Integer         quantity
    ) {
        this.user       = Objects.requireNonNull(user);
        this.variant    = Objects.requireNonNull(variant);
        this.quantity   = this.validateQuantity(quantity);
    }

    public void setQuantity(Integer quantity) {
        this.quantity = this.validateQuantity(quantity);
    }

    private Integer validateQuantity(Integer quantity) {
        return ValidationUtils.requireNonNegative(quantity, "La cantidad ingresada no puede ser menor a cero", true);
    }

    public Users getUser() { return this.user; }
    public ProductVariant getVariant() { return this.variant; }
    public Integer getQuantity() { return this.quantity; }
}
