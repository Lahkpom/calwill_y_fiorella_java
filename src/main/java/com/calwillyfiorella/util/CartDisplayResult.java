package com.calwillyfiorella.util;

import com.calwillyfiorella.model.CartItem;

import java.util.List;

public record CartDisplayResult(
        List<Integer> indexes,
        List<CartItem> items
) {}
