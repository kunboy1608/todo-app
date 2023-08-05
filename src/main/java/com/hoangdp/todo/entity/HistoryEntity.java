package com.hoangdp.todo.entity;

import java.time.Instant;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public class HistoryEntity {
    protected Instant createdOn;
    protected Long createdBy;
    protected Instant lastModifiedOn;
    protected Long lastModifiedBy;
}
