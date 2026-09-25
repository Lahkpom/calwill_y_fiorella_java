package com.calwillyfiorella.old.model2;

import java.time.LocalDateTime;

import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.util.ValidationUtils;

public class Size extends BaseEntity {
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
                sizeIdSeq + 1,
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
        this.sizeId     = ValidationUtils.requireValidIntegerIdBySeq(sizeIdSeq, sizeId);
        this.size       = this.validateSize(size);
        this.sizeDesc   = this.validateSizeDesc(sizeDesc);
        this.sizeOrder  = this.validateSizeOrder(sizeOrder);

        sizeIdSeq++;
    }

    private String validateSize(String size) {
        return ValidationUtils.requireNonBlank(size, "El talle no puede ser nulo");
    }

    private String validateSizeDesc(String sizeDesc) {
        return ValidationUtils.requireNonBlank(sizeDesc, "La descripción del talle no puede ser nulo");
    }

    private Integer validateSizeOrder(Integer sizeOrder) {
        return ValidationUtils.requireNonNegative(sizeOrder, "El orden del talle no puede ser nulo");
    }

    @Override
    public String toString() {
        return String.format("{ Talle: %s, Descripción: %s, Estado: %s }",
                this.size,
                this.sizeDesc,
                this.rowStatus
        );
    }

    public void setSize(String size) {
        this.size = this.validateSize(size);
        this.afterUpdate();
    }
    public void setSizeDesc(String sizeDesc) {
        this.sizeDesc = this.validateSizeDesc(sizeDesc);
        this.afterUpdate();
    }
    public void setSizeOrder(Integer sizeOrder) {
        this.sizeOrder = this.validateSizeOrder(sizeOrder);
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