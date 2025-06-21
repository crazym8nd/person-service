package com.bnm.personservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "addresses", schema = "person")
@Getter
@Setter
@Audited
public class AddressEntity extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private CountryEntity country;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "archived")
    private Instant archived;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;
} 