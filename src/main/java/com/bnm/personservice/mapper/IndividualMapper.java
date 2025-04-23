package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.User;
import com.bnm.personservice.model.Individual;
import com.bnm.personservice.model.IndividualCreate;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

@Component
public class IndividualMapper {

  public Individual toDto(final com.bnm.personservice.entity.Individual entity) {
    final Individual dto = new Individual();
    dto.setId(entity.getId());
    dto.setUserId(entity.getUser().getId());
    dto.setCreated(convertToOffsetDateTime(entity.getCreated()));
    dto.setUpdated(convertToOffsetDateTime(entity.getUpdated()));
    dto.setPassportNumber(entity.getPassportNumber());
    dto.setPhoneNumber(entity.getPhoneNumber());
    dto.setEmail(entity.getEmail());
    return dto;
  }

  private OffsetDateTime convertToOffsetDateTime(final Instant instant) {
    return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
  }

  public com.bnm.personservice.entity.Individual toEntity(final IndividualCreate dto,
      final User user) {
    final com.bnm.personservice.entity.Individual entity = new com.bnm.personservice.entity.Individual();
    entity.setPassportNumber(dto.getPassportNumber());
    entity.setPhoneNumber(dto.getPhoneNumber());
    entity.setEmail(dto.getEmail());
    entity.setUser(user);
    return entity;
  }
} 