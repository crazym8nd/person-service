package com.bnm.personservice.model;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class IndividualAuditDTO {

  private UUID id;
  private UUID userId;
  private String passportNumber;
  private String phoneNumber;
  private String email;
  private Instant createdAt;
  private String createdBy;
  private Instant updatedAt;
  private String updatedBy;

  // Информация о пользователе
  private String firstName;
  private String lastName;
  private String status;
  private UUID addressId;
} 