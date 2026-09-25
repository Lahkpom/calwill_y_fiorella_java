package com.calwillyfiorella.service;

import com.calwillyfiorella.model.Color;
import com.calwillyfiorella.repository.ColorRepository;

import java.util.List;
import java.util.Objects;

public class ColorService {
    private final ColorRepository colorRepository;

    public ColorService(ColorRepository colorRepository) { this.colorRepository = colorRepository; }

    public List<Color> getAll() { return colorRepository.findAll(); }

    public void createColor(
            String colorName,
            String colorDesc,
            String colorCode
    ) {
        AuthService.checkActualUserIsAdmin();

        validateColorName(colorName);
        validateColorCode(colorCode);

        colorRepository.save(
                new Color(
                        colorName,
                        colorDesc,
                        colorCode
                )
        );
    }

    private void validateColorName(String colorName) {
        if (this.colorRepository.findByName(colorName).isPresent())
            throw new IllegalStateException("Color with name " + colorName + " already exists.");
    }

    private void validateColorCode(String colorCode) {
        if (this.colorRepository.findByHexCod(colorCode).isPresent())
            throw new IllegalStateException("Color with code " + colorCode + " already exists.");
    }

    public void removeColor(Color color) {
        AuthService.checkActualUserIsAdmin();
        colorRepository.delete(
                Objects.requireNonNull(color,  "color must not be null")
        );
    }

    public void updateColorName(Color color, String colorName) {
        AuthService.checkActualUserIsAdmin();
        color.setColorName(colorName);
    }

    public void updateColorDesc(Color color, String colorDesc) {
        AuthService.checkActualUserIsAdmin();
        color.setColorDesc(colorDesc);
    }

    public void updateColorCode(Color color, String colorCode) {
        AuthService.checkActualUserIsAdmin();
        color.setColorCode(colorCode);
    }
}
