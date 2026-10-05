package com.calwillyfiorella.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.calwillyfiorella.exception.CartItemDoesNotExistException;
import com.calwillyfiorella.exception.InsufficientStockException;
import com.calwillyfiorella.model.Cart;
import com.calwillyfiorella.model.CartItem;
import com.calwillyfiorella.model.ProductVariant;
import com.calwillyfiorella.model.Users;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.repository.CartRepository;

public class CartService {
    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public void addItem(Users user, ProductVariant variant, Integer quantity) {
        Objects.requireNonNull(variant, "No puede ingresar una variante nula al carrito.");

        checkStock(variant.getStock(),  quantity);

        cartRepository.findItem(user, variant)
                .ifPresentOrElse(
                        existingItem -> {
                            int newTotal = existingItem.getQuantity() + quantity;

                            try {
                                checkStock(variant.getStock(), newTotal);
                            } catch (InsufficientStockException e) {
                                System.err.format("### El Item ingresado ya se encontraba en el carrito y al sumar las cantidades existente y nueva superan el stock disponible, por lo que se ajusta la cantidad al máximo permitido.");
                                newTotal = existingItem.getVariant().getStock();
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
        this.updateQuantity(cartRepository.findItem(user, variant), quantity);
    }

    private void updateQuantity(Optional<CartItem> itemOptional, Integer quantity) {
        CartItem cartItem = ifItemExists(itemOptional);

        checkStock(cartItem.getVariant().getStock(), quantity);
        cartItem.setQuantity(quantity);
    }

    public void removeItem(Users user, ProductVariant variant) {
        cartRepository.findItem(user, variant).ifPresent(cartRepository::delete);
    }

    private CartItem ifItemExists(Optional<CartItem> itemOptional) {
        return itemOptional.orElseThrow(CartItemDoesNotExistException::new);
    }

    public void clearUserItems(Users user) {
        this.cartRepository.clearByUser(
                Objects.requireNonNull(user, "El usuario no puede ser nulo.")
        );
    }

    public Cart getUserCart(Users user) {
        Objects.requireNonNull(user, "El usuario no puede ser nulo.");

        Cart cart = cartRepository.findByUser(user);
        List<CartItem> validItems = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            ProductVariant variant = item.getVariant();

            if (variant.getRowStatus() != RowStatus.ACTIVE || variant.getStock() <= 0) {
                cartRepository.delete(item);
                continue;
            }

            if (item.getQuantity() > variant.getStock()) item.setQuantity(variant.getStock());

            validItems.add(item);
        }
        return new Cart(cart.getUserId(), validItems);
    }
}
