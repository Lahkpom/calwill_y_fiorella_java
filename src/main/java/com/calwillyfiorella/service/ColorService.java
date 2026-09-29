package com.calwillyfiorella.service;

import com.calwillyfiorella.exception.ColorAlreadyExistException;
import com.calwillyfiorella.exception.ColorDoesNotExistException;
import com.calwillyfiorella.model.Color;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.repository.ColorRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
            throw new ColorAlreadyExistException("Color with name " + colorName + " already exists.");
    }

    private void validateColorCode(String colorCode) {
        if (this.colorRepository.findByHexCod(colorCode).isPresent())
            throw new ColorAlreadyExistException("Color with code " + colorCode + " already exists.");
    }

    public void updateColorStatus(Integer colorId, RowStatus newStatus) {
        AuthService.checkActualUserIsAdmin();

        ifColorExits(colorRepository.findById(colorId))
                .setRowStatus(
                        Objects.requireNonNull(newStatus)
                );
    }

    public void removeColor(Integer colorId) {
        AuthService.checkActualUserIsAdmin();

        colorRepository.delete(
                ifColorExits(
                        colorRepository.findById(colorId)
                )
        );
    }

    private Color ifColorExits(Optional<Color> colorOptional) {
        return colorOptional.orElseThrow(ColorDoesNotExistException::new);
    }

    public void updateColorName(Integer colorId, String colorName) {
        AuthService.checkActualUserIsAdmin();

        validateColorName(colorName);

        ifColorExits(colorRepository.findById(colorId))
                .setColorName(colorName);
    }

    public void updateColorDesc(Integer colorId, String colorDesc) {
        AuthService.checkActualUserIsAdmin();

        ifColorExits(colorRepository.findById(colorId))
                .setColorDesc(colorDesc);
    }

    public void updateColorCode(Integer colorId, String colorCode) {
        AuthService.checkActualUserIsAdmin();

        ifColorExits(colorRepository.findById(colorId))
                .setColorCode(colorCode);
    }
}
