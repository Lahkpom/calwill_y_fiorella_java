package com.calwillyfiorella.repository;

import com.calwillyfiorella.model.CartItem;
import com.calwillyfiorella.model.ProductVariant;
import com.calwillyfiorella.model.Users;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CartRepository {
    private final List<CartItem> cart;

    public CartRepository() {
        this.cart = new ArrayList<>();
    }

    public void save(CartItem cartItem) { this.cart.add(cartItem); }

    public List<CartItem> findByUser(Users user) {
        return this.cart.stream()
                .filter(item -> item.getUser().equals(user))
                .toList();
    }

    public Optional<CartItem> findItem(Users user, ProductVariant variant) {
        return this.cart.stream()
                .filter(item -> item.getUser().equals(user) && item.getVariant().equals(variant))
                .findFirst();
    }

    public Optional<CartItem> findItem(UUID itemId) {
        return this.cart.stream()
                .filter(item -> item.getItemId().equals(itemId))
                .findFirst();
    }

    public void clearByUser(Users user) {
        this.cart.removeIf(item -> item.getUser().equals(user));
    }

    public void delete(CartItem cartItem) { this.cart.remove(cartItem); }
}
