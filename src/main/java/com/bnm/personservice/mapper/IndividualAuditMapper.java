package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.Individual;
import com.bnm.personservice.model.IndividualAudit;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.hibernate.envers.RevisionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface IndividualAuditMapper {

  @Mapping(source = "entity.id", target = "id")
  @Mapping(source = "entity.user.id", target = "userId")
  @Mapping(source = "entity.passportNumber", target = "passportNumber")
  @Mapping(source = "entity.phoneNumber", target = "phoneNumber")
  @Mapping(source = "entity.email", target = "email")
  @Mapping(source = "revisionNumber", target = "revisionNumber", qualifiedByName = "numberToInteger")
  @Mapping(source = "revisionType", target = "revisionType", qualifiedByName = "mapRevisionType")
  @Mapping(source = "revisionInstant", target = "revisionInstant", qualifiedByName = "instantToOffsetDateTime")
  IndividualAudit toDto(Individual entity, Number revisionNumber, RevisionType revisionType,
      Instant revisionInstant);

  @Named("numberToInteger")
  default Integer numberToInteger(final Number number) {
    return number != null ? number.intValue() : null;
  }

  @Named("mapRevisionType")
  default IndividualAudit.RevisionTypeEnum mapRevisionType(final RevisionType revisionType) {
    return revisionType != null ? IndividualAudit.RevisionTypeEnum.fromValue(revisionType.name())
        : null;
  }

  @Named("instantToOffsetDateTime")
  default OffsetDateTime instantToOffsetDateTime(final Instant instant) {
    return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
  }
} 