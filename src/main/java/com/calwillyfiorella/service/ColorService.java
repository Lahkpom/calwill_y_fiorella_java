package com.calwillyfiorella.service;

import com.calwillyfiorella.exception.ColorAlreadyExistException;
import com.calwillyfiorella.exception.ColorDoesNotExistException;
import com.calwillyfiorella.exception.InvalidHexColorCodeException;
import com.calwillyfiorella.model.BaseEntity;
import com.calwillyfiorella.model.Color;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.repository.ColorRepository;
import com.calwillyfiorella.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class ColorService {
    private static final Pattern HEX_PATTERN = Pattern.compile("^#[a-zA-Z0-9]{6}$");

    private final ColorRepository colorRepository;

    public ColorService(ColorRepository colorRepository) { this.colorRepository = colorRepository; }

    public List<Color> getAll() { return colorRepository.findAll(); }

    public Color getColorById(Integer colorId) { return ifColorExits(this.colorRepository.findById(colorId)); }
    public Color getColorByName(String colorName) { return ifColorExits(this.colorRepository.findByName(colorName)); }
    public Color getColorByHexCode(String colorCode) { return ifColorExits(this.colorRepository.findByHexCod(colorCode)); }

    public void createColor(Color color) {
        AuthService.checkActualUserIsAdmin();

        if (color.getId() == null) {
            // El id se lo asigna el repository antes de guardarlo
            color.setRowStatus(RowStatus.ACTIVE);
            color.setCreatedAt(LocalDateTime.now());
            color.setUpdatedAt(null);
        } else {
            validateColorId(color.getId());
            BaseEntity.validateRowStatus(color.getRowStatus());
            BaseEntity.validateCreatedAt(color.getCreatedAt());
        }

        validateColorName(color.getName());
        validateColorDesc(color.getDesc());
        validateColorCode(color.getCode());

        colorRepository.save(color);
    }

    public void updateColor(Integer colorId, Color newColorData) {
        AuthService.checkActualUserIsAdmin();

        Color currentColorData = getColorById(colorId);

        validateColorName(newColorData.getName(), colorId);
        validateColorDesc(newColorData.getDesc());
        validateColorCode(newColorData.getCode(), colorId);
        BaseEntity.validateRowStatus(newColorData.getRowStatus());

        currentColorData.setName(newColorData.getName());
        currentColorData.setDesc(newColorData.getDesc());
        currentColorData.setCode(newColorData.getCode());
        currentColorData.setRowStatus(newColorData.getRowStatus());
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

    private void validateColorId(Integer colorId) {
        Objects.requireNonNull(colorId, "Color ID cannot be null.");

        if (getColorById(colorId) != null)
            throw new ColorAlreadyExistException("El ID del color ya existe en la lista!");
    }
    private void validateColorName(String colorName) {
        validateColorName(colorName, null);
    }
    private void validateColorName(String colorName, Integer currentColorId) {
        ValidationUtils.requireNonBlank(colorName, "El nombre del color no puede ser nulo.");

        Optional<Color> existingColor = this.colorRepository.findByName(colorName);

        if (existingColor.isPresent() && (currentColorId == null || !existingColor.get().getId().equals(currentColorId)))
            throw new ColorAlreadyExistException("Color with name " + colorName + " already exists.");
    }
    private void validateColorDesc(String colorCode) {
        ValidationUtils.requireNonBlank(colorCode, "La descripción del color no puede ser nulo");
    }
    private void validateColorCode(String colorCode) {
        validateColorCode(colorCode, null);
    }
    private void validateColorCode(String colorCode, Integer currentColorId) {
        ValidationUtils.requireNonBlank(colorCode, "El código del color no puede ser nulo o vacío.");

        if (!HEX_PATTERN.matcher(colorCode).matches())
            throw new InvalidHexColorCodeException();

        Optional<Color> existingColor = this.colorRepository.findByHexCod(colorCode);

        if (existingColor.isPresent() && (currentColorId == null || !existingColor.get().getId().equals(currentColorId)))
            throw new ColorAlreadyExistException("Color with code " + colorCode + " already exists.");
    }
}
