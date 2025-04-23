package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.Address;
import com.bnm.personservice.model.User;
import com.bnm.personservice.model.UserCreate;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public User toDto(final com.bnm.personservice.entity.User entity) {
    final User dto = new User();
    dto.setId(entity.getId());
    dto.setSecretKey(entity.getSecretKey());
    dto.setCreated(convertToOffsetDateTime(entity.getCreated()));
    dto.setUpdated(convertToOffsetDateTime(entity.getUpdated()));
    dto.setFirstName(entity.getFirstName());
    dto.setLastName(entity.getLastName());
    dto.setVerifiedAt(convertToOffsetDateTime(entity.getVerifiedAt()));
    dto.setArchivedAt(convertToOffsetDateTime(entity.getArchivedAt()));
    dto.setStatus(entity.getStatus());
    dto.setAddressId(entity.getAddress().getId());
    return dto;
  }

  private OffsetDateTime convertToOffsetDateTime(final Instant instant) {
    return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
  }

  public com.bnm.personservice.entity.User toEntity(final UserCreate dto,
      final Address address) {
    final com.bnm.personservice.entity.User entity = new com.bnm.personservice.entity.User();
    entity.setFirstName(dto.getFirstName());
    entity.setLastName(dto.getLastName());
    entity.setAddress(address);
    return entity;
  }
} 