package com.bnm.personservice.mapper;

import com.bnm.personservice.model.Country;
import com.bnm.personservice.model.CountryCreate;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

@Component
public class CountryMapper {

  public Country toDto(final com.bnm.personservice.entity.Country entity) {
    final Country dto = new Country();
    dto.setId(entity.getId());
    dto.setCreated(convertToOffsetDateTime(entity.getCreated()));
    dto.setUpdated(convertToOffsetDateTime(entity.getUpdated()));
    dto.setName(entity.getName());
    dto.setAlpha2(entity.getAlpha2());
    dto.setAlpha3(entity.getAlpha3());
    dto.setStatus(entity.getStatus());
    return dto;
  }

  private OffsetDateTime convertToOffsetDateTime(final Instant instant) {
    return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
  }

  public com.bnm.personservice.entity.Country toEntity(final CountryCreate dto) {
    final com.bnm.personservice.entity.Country entity = new com.bnm.personservice.entity.Country();
    entity.setName(dto.getName());
    entity.setAlpha2(dto.getAlpha2());
    entity.setAlpha3(dto.getAlpha3());
    entity.setStatus(dto.getStatus());
    return entity;
  }
} 