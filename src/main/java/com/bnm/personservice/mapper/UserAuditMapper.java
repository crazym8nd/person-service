package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.User;
import com.bnm.personservice.model.UserAudit;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.hibernate.envers.RevisionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserAuditMapper {

  @Mapping(source = "entity.id", target = "id")
  @Mapping(source = "entity.firstName", target = "firstName")
  @Mapping(source = "entity.lastName", target = "lastName")
  @Mapping(source = "entity.status", target = "status")
  @Mapping(source = "entity.verifiedAt", target = "verifiedAt", qualifiedByName = "instantToOffsetDateTime")
  @Mapping(source = "entity.archivedAt", target = "archivedAt", qualifiedByName = "instantToOffsetDateTime")
  @Mapping(source = "entity.address.id", target = "addressId")
  @Mapping(source = "revisionNumber", target = "revisionNumber", qualifiedByName = "numberToInteger")
  @Mapping(source = "revisionType", target = "revisionType", qualifiedByName = "mapRevisionType")
  @Mapping(source = "revisionInstant", target = "revisionInstant", qualifiedByName = "instantToOffsetDateTime")
  UserAudit toDto(User entity, Number revisionNumber, RevisionType revisionType,
      Instant revisionInstant);

  @Named("numberToInteger")
  default Integer numberToInteger(final Number number) {
    return number != null ? number.intValue() : null;
  }

  @Named("mapRevisionType")
  default UserAudit.RevisionTypeEnum mapRevisionType(final RevisionType revisionType) {
    return revisionType != null ? UserAudit.RevisionTypeEnum.fromValue(revisionType.name())
        : null;
  }

  @Named("instantToOffsetDateTime")
  default OffsetDateTime instantToOffsetDateTime(final Instant instant) {
    return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
  }
} 