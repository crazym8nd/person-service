package com.bnm.personservice.model;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class AddressAuditDTO {

  private UUID id;
  private Long countryId;
  private String countryName;
  private String address;
  private String zipCode;
  private String city;
  private String state;
  private Instant archived;
  private Instant createdAt;
  private String createdBy;
  private Instant updatedAt;
  private String updatedBy;
} 