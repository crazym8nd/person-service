package com.bnm.personservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "countries", schema = "person")
@Getter
@Setter
public class Country {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "created", nullable = false)
  private Instant created;

  @Column(name = "updated", nullable = false)
  private Instant updated;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "alpha2", nullable = false, length = 2)
  private String alpha2;

  @Column(name = "alpha3", nullable = false, length = 3)
  private String alpha3;

  @Column(name = "status", nullable = false)
  private String status;
} 