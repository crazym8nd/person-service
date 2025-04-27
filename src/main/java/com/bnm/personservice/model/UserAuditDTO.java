package com.bnm.personservice.model;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class UserAuditDTO {

  private UUID id;
  private UUID addressId;
  private String firstName;
  private String lastName;
  private String status;
  private Instant verifiedAt;
  private Instant archivedAt;
  private Instant createdAt;
  private String createdBy;
  private Instant updatedAt;
  private String updatedBy;
} 