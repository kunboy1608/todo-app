package com.hoangdp.todo.entity;

import java.time.Instant;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class HistoryEntity {
    protected Instant createdOn;
    protected String createdBy;
    protected Instant lastModifiedOn;
    protected String lastModifiedBy;
}
