package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.User;
import com.bnm.personservice.model.Individual;
import com.bnm.personservice.model.IndividualAudit;
import com.bnm.personservice.model.IndividualCreate;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.hibernate.envers.RevisionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface IndividualMapper {

  @Mapping(source = "id", target = "id")
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "createdAt", target = "created", qualifiedByName = "instantToOffsetDateTime")
  @Mapping(source = "updatedAt", target = "updated", qualifiedByName = "instantToOffsetDateTime")
  @Mapping(source = "passportNumber", target = "passportNumber")
  @Mapping(source = "phoneNumber", target = "phoneNumber")
  @Mapping(source = "email", target = "email")
  Individual toDto(com.bnm.personservice.entity.Individual entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(source = "user", target = "user")
  com.bnm.personservice.entity.Individual toEntity(IndividualCreate dto, User user);

  @Mapping(source = "entity.id", target = "id")
  @Mapping(source = "entity.user.id", target = "userId")
  @Mapping(source = "entity.passportNumber", target = "passportNumber")
  @Mapping(source = "entity.phoneNumber", target = "phoneNumber")
  @Mapping(source = "entity.email", target = "email")
  @Mapping(source = "revisionNumber", target = "revisionNumber", qualifiedByName = "numberToInteger")
  @Mapping(source = "revisionType", target = "revisionType", qualifiedByName = "mapRevisionType")
  @Mapping(source = "revisionInstant", target = "revisionInstant", qualifiedByName = "instantToOffsetDateTime")
  IndividualAudit toAuditDto(com.bnm.personservice.entity.Individual entity, Number revisionNumber,
      RevisionType revisionType, Instant revisionInstant);

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