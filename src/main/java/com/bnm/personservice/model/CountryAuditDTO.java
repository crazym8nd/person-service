package com.bnm.personservice.model;

import java.time.Instant;
import lombok.Data;

@Data
public class CountryAuditDTO {

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