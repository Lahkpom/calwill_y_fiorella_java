package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.util.ValidationUtils;

import java.util.Objects;
import java.util.UUID;

public class CartItem {
    private final UUID              itemId;
    private final Users             user;
    private final ProductVariant    variant;

    private Integer quantity;

    public CartItem(
            Users           user,
            ProductVariant  variant,
            Integer         quantity
    ) {
        this(
                UUID.randomUUID(),
                user,
                variant,
                quantity
        );
    }
    public CartItem(
            UUID            itemId,
            Users           user,
            ProductVariant  variant,
            Integer         quantity
    ) {
        Objects.requireNonNull(itemId);
        Objects.requireNonNull(user);
        Objects.requireNonNull(variant);

        if (variant.getRowStatus() != RowStatus.ACTIVE)
            throw new IllegalArgumentException("No puede ingresar una variante que no está ACTIVE al carrito de compras!");

        this.itemId     = itemId;
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

    public UUID getItemId() { return itemId; }
    public Users getUser() { return this.user; }
    public ProductVariant getVariant() { return this.variant; }
    public Integer getQuantity() { return this.quantity; }
}
