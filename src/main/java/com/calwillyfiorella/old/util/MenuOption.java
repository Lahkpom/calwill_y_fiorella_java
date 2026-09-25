package com.calwillyfiorella.util;

public record MenuOption(String label, Runnable action) {

    public MenuOption {
        ValidationUtils.requireNonBlank(label, "label cannot be blank!");
    }

    public static MenuOption of(String label, Runnable action) {
        return new MenuOption(label, action);
    }
}