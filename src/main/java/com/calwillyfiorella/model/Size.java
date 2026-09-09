package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.RowStatus;
import java.time.LocalDateTime;

public class Size extends  BaseEntity{
    private static Integer sizeIdSeq = 0;

    private final Integer sizeId;

    private String  size;
    private String  sizeDesc;
    private Integer sizeOrder;

    public Size(
            String  size,
            String  sizeDesc,
            Integer sizeOrder
    ) {
        this(
                sizeIdSeq++,
                size,
                sizeDesc,
                sizeOrder,
                RowStatus.ACTIVE,
                LocalDateTime.now(),
                null
        );
    }

    /* Este constructor solo debe ser llamado para cargar desde registros provenientes de la DB */
    public Size(
            Integer         sizeId,
            String          size,
            String          sizeDesc,
            Integer         sizeOrder,
            RowStatus       rowStatus,
            LocalDateTime   createdAt,
            LocalDateTime   updatedAt
    ) {
        super(rowStatus, createdAt, updatedAt);
        this.sizeId     = sizeId;
        this.size       = size;
        this.sizeDesc   = sizeDesc;
        this.sizeOrder  = sizeOrder;
    }

    @Override
    public String toString() {
        return String.format("Size: %s - Desc: %s", this.size, this.sizeDesc);
    }

    public void setSize(String size) {
        this.size = size;
        this.afterUpdate();
    }
    public void setSizeDesc(String sizeDesc) {
        this.sizeDesc = sizeDesc;
        this.afterUpdate();
    }
    public void setSizeOrder(Integer sizeOrder) {
        this.sizeOrder = sizeOrder;
        this.afterUpdate();
    }

    public Integer  getSizeId   () { return this.sizeId; }
    public String   getSize     () {
        return this.size;
    }
    public String   getSizeDesc () {
        return this.sizeDesc;
    }
    public Integer  getSizeOrder() {
        return this.sizeOrder;
    }
}