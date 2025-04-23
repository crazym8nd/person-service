package com.bnm.personservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "countries", schema = "person")
@Getter
@Setter
public class Country extends BaseEntity {

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