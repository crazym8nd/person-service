package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.User;
import com.bnm.personservice.model.UserAudit;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.hibernate.envers.RevisionType;
import org.springframework.stereotype.Component;

@Component
public class UserAuditMapper {

  private OffsetDateTime convertToOffsetDateTime(final Instant instant) {
    return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
  }

  public UserAudit toDto(final User entity, final Number revisionNumber,
      final RevisionType revisionType, final Instant revisionInstant) {
    if (entity == null) {
      return null;
    }

    final UserAudit dto = new UserAudit();
    dto.setId(entity.getId());
    dto.setFirstName(entity.getFirstName());
    dto.setLastName(entity.getLastName());
    dto.setStatus(entity.getStatus());
    dto.setVerifiedAt(convertToOffsetDateTime(entity.getVerifiedAt()));
    dto.setArchivedAt(convertToOffsetDateTime(entity.getArchivedAt()));
    if (entity.getAddress() != null) {
      dto.setAddressId(entity.getAddress().getId());
    }

    // Добавляем информацию об аудите
    dto.setRevisionNumber(revisionNumber.intValue());
    dto.setRevisionType(UserAudit.RevisionTypeEnum.fromValue(revisionType.name()));
    dto.setRevisionInstant(convertToOffsetDateTime(revisionInstant));

    return dto;
  }
} 