package com.bnm.personservice.service.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode
public class Country {
    private Long id;
    private String name;
    private String alpha2;
    private String alpha3;
    private String status;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
} 