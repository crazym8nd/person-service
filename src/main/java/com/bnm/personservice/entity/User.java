package com.bnm.personservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Audited
@Table(name = "users", schema = "person")
@Getter
@Setter
public class User extends BaseEntity {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "secret_key", length = 32)
  private String secretKey;

  @Column(name = "first_name", length = 32)
  private String firstName;

  @Column(name = "last_name", length = 32)
  private String lastName;

  @Column(name = "verified_at", nullable = false)
  private Instant verifiedAt;

  @Column(name = "archived_at", nullable = false)
  private Instant archivedAt;

  @Column(name = "status", length = 64)
  private String status;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "address_id")
  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  private Address address;

  @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
  private Individual individual;
} 