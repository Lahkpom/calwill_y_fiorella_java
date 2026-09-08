package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.RowStatus;
import java.time.LocalDateTime;

public class Size {
    private Integer         sizeId;
    private String          size;
    private String          sizeDesc;
    private Integer         sizeOrder;
    private RowStatus       rowStatus;
    private LocalDateTime   createdAt;
    private LocalDateTime   updatedAt;

    public Size(
            Integer     sizeId,
            String      size,
            String      sizeDesc,
            Integer     sizeOrder
    ) {
        this.sizeId     = sizeId;
        this.size       = size;
        this.sizeDesc   = sizeDesc;
        this.sizeOrder  = sizeOrder;
    }

    protected void onCreate() {
        this.rowStatus = RowStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setSizeDesc(String sizeDesc) {
        this.sizeDesc = sizeDesc;
    }

    public void setSizeOrder(Integer sizeOrder) {
        this.sizeOrder = sizeOrder;
    }

    public void setRowStatus(RowStatus rowStatus) {
        this.rowStatus = rowStatus;
    }

    public Integer getSizeId() {
        return sizeId;
    }

    public String getSize() {
        return size;
    }

    public String getSizeDesc() {
        return sizeDesc;
    }

    public Integer getSizeOrder() {
        return sizeOrder;
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