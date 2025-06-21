package com.bnm.personservice.service.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode
public class Individual {
    private UUID id;
    private UUID userId;
    private String passportNumber;
    private String phoneNumber;
    private String email;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
} 