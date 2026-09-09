package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.RowStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class Color extends BaseEntity {
    private static Integer colorIdSeq = 0;

    private final Integer   colorId;

    private String colorName;
    private String colorDesc;
    private String colorCode;

    public Color(
            String  colorName,
            String  colorDesc,
            String  colorCode
    ) {
        this(
                colorIdSeq++,
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
        this.colorId    = colorId;
        this.colorName  = colorName;
        this.colorDesc  = colorDesc;
        this.colorCode  = colorCode;
    }

    @Override
    public String toString() {
        return String.format("Color: %s (Cod Hex: %s)", this.colorName, this.colorCode);
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
        this.afterUpdate();
    }
    public void setColorDesc(String colorDesc) {
        this.colorDesc = colorDesc;
        this.afterUpdate();
    }
    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
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