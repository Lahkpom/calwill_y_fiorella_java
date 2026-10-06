package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.Category;
import com.calwillyfiorella.model.enums.NumericSize;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.TargetGender;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class Product extends BaseEntity{
    private UUID        productId;
    private Category    productCategory;
    private String      productName;
    private String      productShortDesc;
    private String      productLongDesc;

    public Product() {}

    // Constructor para NUEVOS productos
    public Product(
            Category    productCategory,
            String      productName,
            String      productShortDesc,
            String      productLongDesc
    ) {
        this(
            UUID.randomUUID(),
            productCategory,
            productName,
            productShortDesc,
            productLongDesc,
            RowStatus.ACTIVE,
            LocalDateTime.now(),
            null
        );
    }

    // Constructor para reconstruir productos DESDE LA DB
    public Product(
            UUID            productId,
            Category        productCategory,
            String          productName,
            String          productShortDesc,
            String          productLongDesc,
            RowStatus       rowStatus,
            LocalDateTime   createdAt,
            LocalDateTime   updatedAt
    ) {
        super(rowStatus, createdAt, updatedAt);
        this.productId        = productId;
        this.productCategory  = productCategory;
        this.productName      = productName;
        this.productShortDesc = productShortDesc;
        this.productLongDesc  = productLongDesc;
    }

    @Override
    public String toString() {
        return String.format(
                "{ Nombre: %s, Categoría: %s, Descripción Corta: %s, Estado: %s }",
                this.productName,
                this.productCategory,
                this.productShortDesc,
                this.rowStatus
        );
    }

    public String toStringComplete(int variantQuantity) {
        return """
            {
                ID          : (%s).
                Nombre      : %s.
                Desc. Corta : %s.
                Desc. Larga : %s.
                Variantes   : %d.
                Estado      : %s.
                Fecha Inicio: %td/%<tm/%<tY (%<tT).
                Última Mod  : %S.
            }""".formatted(
                this.productId,
                this.productName,
                this.productShortDesc,
                this.productLongDesc,
                variantQuantity,
                super.rowStatus,
                super.createdAt,
                (super.updatedAt != null)
                        ? "%td/%<tm/%<tY (%<tT)".formatted(super.updatedAt)
                        : "Sin modificaciones"
        );
    }

    // Getters y Setters
    public UUID     getId       () { return this.productId; }
    public Category getCategory () { return this.productCategory; }
    public String   getName     () { return this.productName; }
    public String   getShortDesc() { return this.productShortDesc; }
    public String   getLongDesc () { return this.productLongDesc; }

    public void setId(UUID id) {
        this.productId = id;
        this.afterUpdate();
    }
    public void setCategory(Category productCategory) {
        this.productCategory = productCategory;
        this.afterUpdate();
    }
    public void setName(String productName) {
        this.productName = productName;
        this.afterUpdate();
    }
    public void setShortDesc(String productShortDesc) {
        this.productShortDesc = productShortDesc;
        this.afterUpdate();
    }
    public void setLongDesc(String productLongDesc) {
        this.productLongDesc = productLongDesc;
        this.afterUpdate();
    }
}