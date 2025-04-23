package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.Country;
import com.bnm.personservice.model.Address;
import com.bnm.personservice.model.AddressCreate;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

  public Address toDto(final com.bnm.personservice.entity.Address entity) {
    final Address dto = new Address();
    dto.setId(entity.getId());
    dto.setCreated(convertToOffsetDateTime(entity.getCreated()));
    dto.setUpdated(convertToOffsetDateTime(entity.getUpdated()));
    dto.setCountryId(entity.getCountry().getId());
    dto.setAddress(entity.getAddress());
    dto.setZipCode(entity.getZipCode());
    dto.setArchived(JsonNullable.of(convertToOffsetDateTime(entity.getArchived())));
    dto.setCity(entity.getCity());
    dto.setState(entity.getState());
    return dto;
  }

  private OffsetDateTime convertToOffsetDateTime(final Instant instant) {
    return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
  }

  public com.bnm.personservice.entity.Address toEntity(final AddressCreate dto,
      final Country country) {
    final com.bnm.personservice.entity.Address entity = new com.bnm.personservice.entity.Address();
    entity.setCountry(country);
    entity.setAddress(dto.getAddress());
    entity.setZipCode(dto.getZipCode());
    entity.setCity(dto.getCity());
    entity.setState(dto.getState());
    entity.setArchived(Instant.now());
    return entity;
  }
} 