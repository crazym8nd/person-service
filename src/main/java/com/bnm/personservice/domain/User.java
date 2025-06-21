package com.bnm.personservice.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode
public class User {
    private UUID id;
    private String secretKey;
    private String firstName;
    private String lastName;
    private String status;
    private Instant verifiedAt;
    private Instant archivedAt;
    private Address address;
    private Individual individual;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
} 