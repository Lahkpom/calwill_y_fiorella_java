package com.calwillyfiorella.model;

import com.calwillyfiorella.util.ValidationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cart {
    private final List<CartItem> cartItems;

    public Cart() {
        this.cartItems = new ArrayList<>();
    }

    public void addItem(Users user, ProductVariant variant, Integer quantity) {
        this.cartItems.add(new CartItem(user, variant, quantity));
    }

    public void removeItem(Users user, ProductVariant variant) {
        this.cartItems.removeIf(item -> item.getUser().equals(user) && item.getVariant().equals(variant));
    }

    public void updateQuantity(Users user, ProductVariant variant, Integer quantity) {
        CartItem cartItem = this.getCartItem(user, variant);

        if (quantity > cartItem.getVariant().getVariantStock())
            throw new IllegalArgumentException("La cantidad ingresada supera el stock disponible de esta variante.");

        cartItem.setQuantity(quantity);
    }

    private CartItem getCartItem(Users user, ProductVariant variant) {
        return this.cartItems.stream()
                .filter(item -> item.getUser().equals(user) && item.getVariant().equals(variant))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El ítem no existe en el carrito."));
    }

    public void clearUserItems(Users user) {
        this.cartItems.removeIf(item -> item.getUser().equals(user));
    }

    public List<CartItem> getUserItems(Users user) {
        return this.cartItems.stream().filter(item -> item.getUser().equals(user)).toList();
    }
}
