package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.RowStatus;
import java.time.LocalDateTime;

public class Color {
    private Integer         colorId;
    private String          colorName;
    private String          colorDesc;
    private String          colorCode;
    private RowStatus       rowStatus;
    private LocalDateTime   createdAt;
    private LocalDateTime   updatedAt;

    public Color (
            Integer         colorId,
            String          colorName,
            String          colorDesc,
            String          colorCode
    ) {
        this.colorId    = colorId;
        this.colorName  = colorName;
        this.colorDesc  = colorDesc;
        this.colorCode  = colorCode;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public void setColorDesc(String colorDesc) {
        this.colorDesc = colorDesc;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public void setRowStatus(RowStatus rowStatus) {
        this.rowStatus = rowStatus;
    }

    public Integer getColorId() {
        return colorId;
    }

    public String getColorName() {
        return colorName;
    }

    public String getColorDesc() {
        return colorDesc;
    }

    public String getColorCode() {
        return colorCode;
    }

    public RowStatus getRowStatus() {
        return rowStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}