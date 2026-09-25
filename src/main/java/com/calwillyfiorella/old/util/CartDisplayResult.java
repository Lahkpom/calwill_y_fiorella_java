package com.calwillyfiorella.util;

import java.util.List;

import com.calwillyfiorella.model.CartItem;

public record CartDisplayResult(
        List<Integer> indexes,
        List<CartItem> items
) {}