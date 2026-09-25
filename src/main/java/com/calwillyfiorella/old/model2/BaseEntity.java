package com.calwillyfiorella.old.model2;

import java.time.LocalDateTime;

import com.calwillyfiorella.model.enums.RowStatus;

public abstract class BaseEntity {
    protected RowStatus rowStatus;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

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

    private void setRowStatus(RowStatus rowStatus) {
        this.rowStatus = rowStatus;
        afterUpdate();
    }

    public void deactive() {
       this.setRowStatus(RowStatus.INACTIVE);
    }

    public void activate() {
        this.setRowStatus(RowStatus.ACTIVE);
    }

    public void delete() {
        this.setRowStatus(RowStatus.DELETED);
    }

    public RowStatus getRowStatus() { return rowStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}