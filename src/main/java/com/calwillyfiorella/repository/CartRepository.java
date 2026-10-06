package com.calwillyfiorella.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.calwillyfiorella.model.Cart;
import com.calwillyfiorella.model.CartItem;
import com.calwillyfiorella.model.ProductVariant;
import com.calwillyfiorella.model.Users;

public class CartRepository {
    private final List<CartItem> cart;

    public CartRepository() {
        this.cart = new ArrayList<>();
    }

    public void save(CartItem cartItem) { this.cart.add(cartItem); }

    public Cart findByUser(Users user) {
        List<CartItem> userItems = this.cart.stream()
                .filter(item -> item.getUser() != null && item.getUser().equals(user))
                .toList();
        return new Cart(user.getId(), userItems);
    }

    public Optional<CartItem> findItem(Users user, ProductVariant variant) {
        return this.cart.stream()
                .filter(item -> item.getUser().equals(user) && item.getVariant().equals(variant))
                .findFirst();
    }

    public void clearByUser(Users user) {
        this.cart.removeIf(item -> item.getUser().equals(user));
    }

    public void delete(CartItem cartItem) { this.cart.remove(cartItem); }
}
