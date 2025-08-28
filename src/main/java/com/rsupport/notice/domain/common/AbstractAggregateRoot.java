package com.rsupport.notice.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class AbstractAggregateRoot {
    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime createdAt;
    @LastModifiedDate
    protected LocalDateTime updatedAt;
    @Version
    protected Long version;
}
