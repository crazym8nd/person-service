package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.Address;
import com.bnm.personservice.model.AddressAudit;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.hibernate.envers.RevisionType;
import org.springframework.stereotype.Component;

@Component
public class AddressAuditMapper {

  public AddressAudit toDto(final Address entity, final Number revisionNumber,
      final RevisionType revisionType, final Instant revisionInstant) {
    if (entity == null) {
      return null;
    }

    final AddressAudit dto = new AddressAudit();
    dto.setId(entity.getId());
    if (entity.getCountry() != null) {
      dto.setCountryId(entity.getCountry().getId());
    }
    dto.setAddress(entity.getAddress());
    dto.setZipCode(entity.getZipCode());
    dto.setCity(entity.getCity());
    dto.setState(entity.getState());
    dto.setArchived(convertToOffsetDateTime(entity.getArchived()));

    // Добавляем информацию об аудите
    dto.setRevisionNumber(revisionNumber.intValue());
    dto.setRevisionType(AddressAudit.RevisionTypeEnum.fromValue(revisionType.name()));
    dto.setRevisionInstant(convertToOffsetDateTime(revisionInstant));

    return dto;
  }

  private OffsetDateTime convertToOffsetDateTime(final Instant instant) {
    return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
  }
} 