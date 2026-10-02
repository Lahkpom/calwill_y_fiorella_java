package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.RowStatus;
import java.time.LocalDateTime;

public abstract class BaseEntity {
    protected RowStatus rowStatus;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    protected BaseEntity() {}

    protected BaseEntity(
            RowStatus       rowStatus,
            LocalDateTime   createdAt,
            LocalDateTime   updatedAt
    ) {
        this.rowStatus = rowStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void afterUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void setRowStatus(RowStatus rowStatus) {
        this.rowStatus = rowStatus;
        afterUpdate();
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public RowStatus getRowStatus() { return rowStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public static void validateRowStatus(RowStatus rowStatus) {
        if (rowStatus == null) {
            throw new NullPointerException("rowStatus cannot be null");
        }
    }
    public static void validateCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) {
            throw new NullPointerException("createdAt cannot be null");
        }
    }
}