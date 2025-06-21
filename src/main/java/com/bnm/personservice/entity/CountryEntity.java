package com.bnm.personservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "countries", schema = "person")
@Getter
@Setter
public class CountryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "alpha2", nullable = false, length = 2)
    private String alpha2;

    @Column(name = "alpha3", length = 3)
    private String alpha3;

    @Column(name = "status", length = 64)
    private String status;
} 