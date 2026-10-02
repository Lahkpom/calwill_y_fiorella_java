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

    public RowStatus getRowStatus() { return rowStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}