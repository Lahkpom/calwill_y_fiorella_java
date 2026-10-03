package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.RowStatus;

import java.time.LocalDateTime;

public class Color extends BaseEntity {
    private Integer colorId;
    private String colorName;
    private String colorDesc;
    private String colorCode;

    public Color() {}

    public Color(
            String  colorName,
            String  colorDesc,
            String  colorCode
    ) {
        this(
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
            String          colorName,
            String          colorDesc,
            String          colorCode,
            RowStatus       rowStatus,
            LocalDateTime   createdAt,
            LocalDateTime   updatedAt
    ) {
        super(rowStatus, createdAt, updatedAt);
        this.colorName  = colorName;
        this.colorDesc  = colorDesc;
        this.colorCode  = colorCode;
    }

    @Override
    public String toString() {
        return String.format("{ Color: %s, Cod Hex: %s, Status: %s }", this.colorName, this.colorCode, this.rowStatus);
    }

    public void setId(Integer id) {
        this.colorId = id;
    }
    public void setName(String colorName) {
        this.colorName  = colorName;
        this.afterUpdate();
    }
    public void setDesc(String colorDesc) {
        this.colorDesc  = colorDesc;
        this.afterUpdate();
    }
    public void setCode(String colorCode) {
        this.colorCode  = colorCode;
        this.afterUpdate();
    }

    public Integer getId() { return this.colorId; }
    public String getName() { return this.colorName; }
    public String getDesc() { return this.colorDesc; }
    public String getCode() { return this.colorCode; }
}