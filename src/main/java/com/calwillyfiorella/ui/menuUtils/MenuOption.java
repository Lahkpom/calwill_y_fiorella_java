package com.calwillyfiorella.ui.menuUtils;

import com.calwillyfiorella.util.ValidationUtils;

public record MenuOption(String label, Runnable action) {

    public MenuOption {
        ValidationUtils.requireNonBlank(label, "Label cannot be blank!");
    }

    public static MenuOption of(String label, Runnable action) {
        return new MenuOption(label, action);
    }
}