package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.Individual;
import com.bnm.personservice.model.IndividualAudit;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.hibernate.envers.RevisionType;
import org.springframework.stereotype.Component;

@Component
public class IndividualAuditMapper {

  private OffsetDateTime convertToOffsetDateTime(final Instant instant) {
    return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
  }

  public IndividualAudit toDto(final Individual entity, final Number revisionNumber,
      final RevisionType revisionType, final Instant revisionInstant) {
    if (entity == null) {
      return null;
    }

    final IndividualAudit dto = new IndividualAudit();
    dto.setId(entity.getId());
    if (entity.getUser() != null) {
      dto.setUserId(entity.getUser().getId());
    }
    dto.setPassportNumber(entity.getPassportNumber());
    dto.setPhoneNumber(entity.getPhoneNumber());
    dto.setEmail(entity.getEmail());

    // Добавляем информацию об аудите
    dto.setRevisionNumber(revisionNumber.intValue());
    dto.setRevisionType(IndividualAudit.RevisionTypeEnum.fromValue(revisionType.name()));
    dto.setRevisionInstant(convertToOffsetDateTime(revisionInstant));

    return dto;
  }
} 