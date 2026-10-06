package com.calwillyfiorella.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Cart {
    private final UUID              userId;
    private final List<CartItem>    items;
    private final BigDecimal        total;

    public Cart(UUID userId, List<CartItem> items) {
        this.userId = userId;
        this.items  = (items != null) ? items : Collections.emptyList();
        this.total  = this.items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public UUID             getUserId   () { return userId; }
    public List<CartItem>   getItems    () { return items; }
    public BigDecimal       getTotal    () { return total; }
    public boolean          isEmpty     () { return items.isEmpty(); }
}