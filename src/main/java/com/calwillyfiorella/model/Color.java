package com.calwillyfiorella.model;

import com.calwillyfiorella.exception.InvalidHexColorCodeException;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class Color extends BaseEntity {
    private static final Pattern HEX_PATTERN = Pattern.compile("^#[a-zA-Z0-9]{6}$");
    private static Integer colorIdSeq = 0;

    private final Integer colorId;

    private String colorName;
    private String colorDesc;
    private String colorCode;

    public Color(
            String  colorName,
            String  colorDesc,
            String  colorCode
    ) {
        this(
                colorIdSeq + 1,
                colorName,
                colorDesc,
                colorCode,
                RowStatus.ACTIVE,
                LocalDateTime.now(),
                null
        );
    }

    /* Este constructor solo debe ser llamado para cargar desde registros provenientes de la DB */
    public Color (
            Integer         colorId,
            String          colorName,
            String          colorDesc,
            String          colorCode,
            RowStatus       rowStatus,
            LocalDateTime   createdAt,
            LocalDateTime   updatedAt
    ) {
        super(rowStatus, createdAt, updatedAt);
        this.colorId    = ValidationUtils.requireValidIntegerIdBySeq(colorIdSeq, colorId);
        this.colorName  = this.validateColorName(colorName);
        this.colorDesc  = this.validateColorDesc(colorDesc);
        this.colorCode  = this.validateColorCode(colorCode);

        colorIdSeq++;
    }

    private String validateColorName(String colorName) {
        return ValidationUtils.requireNonBlank(colorName, "El nombre del color no puede ser nulo");
    }

    private String validateColorDesc(String colorDesc) {
        return ValidationUtils.requireNonBlank(colorDesc, "La descripción del color no puede ser nulo");
    }

    private String validateColorCode(String colorCode) {
        ValidationUtils.requireNonBlank(colorCode, "El código del color no puede ser nulo o vacío");

        if (!HEX_PATTERN.matcher(colorCode).matches())
            throw new InvalidHexColorCodeException();

        return colorCode;
    }

    @Override
    public String toString() {
        return String.format("{ Color: %s, Cod Hex: %s, Status: %s }", this.colorName, this.colorCode, this.rowStatus);
    }

    public void setColorName(String colorName) {
        this.colorName  = this.validateColorName(colorName);
        this.afterUpdate();
    }
    public void setColorDesc(String colorDesc) {
        this.colorDesc  = this.validateColorDesc(colorDesc);
        this.afterUpdate();
    }
    public void setColorCode(String colorCode) {
        this.colorCode  = this.validateColorCode(colorCode);
        this.afterUpdate();
    }

    public Integer  getColorId  () {
        return this.colorId;
    }
    public String   getColorName() {
        return this.colorName;
    }
    public String   getColorDesc() {
        return this.colorDesc;
    }
    public String   getColorCode() {
        return this.colorCode;
    }
}