package com.calwillyfiorella.service;

import com.calwillyfiorella.exception.InsufficientStockException;
import com.calwillyfiorella.model.CartItem;
import com.calwillyfiorella.model.ProductVariant;
import com.calwillyfiorella.model.Users;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.repository.CartRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CartService {
    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public void addItem(Users user, ProductVariant variant, Integer quantity) {
        Objects.requireNonNull(variant, "No puede ingresar una variante nula al carrito.");

        checkStock(variant.getVariantStock(),  quantity);

        cartRepository.findItem(user, variant)
                .ifPresentOrElse(
                        existingItem -> {
                            int newTotal = existingItem.getQuantity() + quantity;

                            try {
                                checkStock(variant.getVariantStock(), newTotal);
                            } catch (InsufficientStockException e) {
                                System.err.format("### El Item ingresado ya se encontraba en el carrito y al sumar las cantidades existente y nueva superan el stock disponible, por lo que se ajusta la cantidad al máximo permitido.");
                                newTotal = existingItem.getVariant().getVariantStock();
                            }

                            existingItem.setQuantity(newTotal);
                        },
                        () -> cartRepository.save(
                                new CartItem(
                                        user,
                                        variant,
                                        quantity
                                )
                        )
                );
    }

    private void checkStock(Integer stock, Integer quantity) {
        if (quantity > stock)
            throw new InsufficientStockException("La cantidad ingresada supera el stock disponible de esta variante.");
    }

    public void updateQuantity(Users user, ProductVariant variant, Integer quantity) {
        CartItem cartItem = cartRepository.findItem(user, variant)
                .orElseThrow(() -> new IllegalArgumentException("El ítem no existe en el carrito."));

        checkStock(cartItem.getVariant().getVariantStock(), quantity);

        cartItem.setQuantity(quantity);
    }

    public void removeItem(Users user, ProductVariant variant) {
        cartRepository.findItem(user, variant).ifPresent(cartRepository::delete);
    }

    public void clearUserItems(Users user) {
        this.cartRepository.clearByUser(
                Objects.requireNonNull(user, "El usuario no puede ser nulo.")
        );
    }

    public List<CartItem> getUserItems(Users user) {
        Objects.requireNonNull(user, "El usuario no puede ser nulo.");

        List<CartItem> rawItems = cartRepository.findByUser(user);
        List<CartItem> validItems = new ArrayList<>();

        for (CartItem item : rawItems) {
            ProductVariant variant = item.getVariant();

            if (variant.getRowStatus() != RowStatus.ACTIVE || variant.getVariantStock() <= 0) {
                cartRepository.delete(item);
                continue;
            }

            if (item.getQuantity() > variant.getVariantStock()) item.setQuantity(variant.getVariantStock());

            validItems.add(item);
        }
        return validItems;
    }
}
