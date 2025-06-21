package com.bnm.personservice.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode
public class Address {
    private UUID id;
    private Country country;
    private String address;
    private String zipCode;
    private Instant archived;
    private String city;
    private String state;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
} 