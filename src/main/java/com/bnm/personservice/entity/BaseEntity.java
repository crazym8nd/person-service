package com.bnm.personservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

  @CreationTimestamp
  @Column(name = "created", nullable = false, updatable = false)
  private Instant created;

  @UpdateTimestamp
  @Column(name = "updated", nullable = false)
  private Instant updated;
} 