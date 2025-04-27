package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.Address;
import com.bnm.personservice.model.User;
import com.bnm.personservice.model.UserAudit;
import com.bnm.personservice.model.UserCreate;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.hibernate.envers.RevisionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(source = "id", target = "id")
  @Mapping(source = "createdAt", target = "created", qualifiedByName = "instantToOffsetDateTime")
  @Mapping(source = "updatedAt", target = "updated", qualifiedByName = "instantToOffsetDateTime")
  @Mapping(source = "firstName", target = "firstName")
  @Mapping(source = "lastName", target = "lastName")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "verifiedAt", target = "verifiedAt", qualifiedByName = "instantToOffsetDateTime")
  @Mapping(source = "archivedAt", target = "archivedAt", qualifiedByName = "instantToOffsetDateTime")
  @Mapping(source = "address.id", target = "addressId")
  User toDto(com.bnm.personservice.entity.User entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "verifiedAt", ignore = true)
  @Mapping(target = "archivedAt", ignore = true)
  @Mapping(target = "individual", ignore = true)
  @Mapping(target = "secretKey", ignore = true)
  @Mapping(source = "address", target = "address")
  com.bnm.personservice.entity.User toEntity(UserCreate dto, Address address);

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
  UserAudit toAuditDto(com.bnm.personservice.entity.User entity, Number revisionNumber,
      RevisionType revisionType, Instant revisionInstant);

  @Named("numberToInteger")
  default Integer numberToInteger(final Number number) {
    return number != null ? number.intValue() : null;
  }

  @Named("mapRevisionType")
  default UserAudit.RevisionTypeEnum mapRevisionType(final RevisionType revisionType) {
    return revisionType != null ? UserAudit.RevisionTypeEnum.fromValue(revisionType.name()) : null;
  }

  @Named("instantToOffsetDateTime")
  default OffsetDateTime instantToOffsetDateTime(final Instant instant) {
    return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
  }
} 