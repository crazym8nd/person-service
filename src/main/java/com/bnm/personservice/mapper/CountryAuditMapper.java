package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.Country;
import com.bnm.personservice.model.CountryAudit;
import java.time.Instant;
import java.time.ZoneOffset;
import org.hibernate.envers.RevisionType;
import org.springframework.stereotype.Component;

@Component
public class CountryAuditMapper {

  public CountryAudit toDto(final Country entity, final Number revisionNumber,
      final RevisionType revisionType, final Instant revisionInstant) {
    if (entity == null) {
      return null;
    }

    final CountryAudit dto = new CountryAudit();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    dto.setAlpha2(entity.getAlpha2());
    dto.setAlpha3(entity.getAlpha3());
    dto.setStatus(entity.getStatus());

    // Добавляем информацию об аудите
    dto.setRevisionNumber(revisionNumber.intValue());
    dto.setRevisionType(CountryAudit.RevisionTypeEnum.fromValue(revisionType.name()));
    dto.setRevisionInstant(
        revisionInstant != null ? revisionInstant.atOffset(ZoneOffset.UTC) : null);

    return dto;
  }
} 