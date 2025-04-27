package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.Country;
import com.bnm.personservice.model.Address;
import com.bnm.personservice.model.AddressAudit;
import com.bnm.personservice.model.AddressCreate;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.hibernate.envers.RevisionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AddressMapper {

  @Mapping(source = "id", target = "id")
  @Mapping(source = "createdAt", target = "created", qualifiedByName = "instantToOffsetDateTime")
  @Mapping(source = "updatedAt", target = "updated", qualifiedByName = "instantToOffsetDateTime")
  @Mapping(source = "address", target = "address")
  @Mapping(source = "zipCode", target = "zipCode")
  @Mapping(source = "city", target = "city")
  @Mapping(source = "state", target = "state")
  @Mapping(source = "archived", target = "archived", qualifiedByName = "instantToOffsetDateTime")
  @Mapping(source = "country.id", target = "countryId")
  Address toDto(com.bnm.personservice.entity.Address entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "archived", ignore = true)
  @Mapping(source = "country", target = "country")
  com.bnm.personservice.entity.Address toEntity(AddressCreate dto, Country country);

  @Mapping(source = "entity.id", target = "id")
  @Mapping(source = "entity.country.id", target = "countryId")
  @Mapping(source = "entity.address", target = "address")
  @Mapping(source = "entity.zipCode", target = "zipCode")
  @Mapping(source = "entity.city", target = "city")
  @Mapping(source = "entity.state", target = "state")
  @Mapping(source = "entity.archived", target = "archived", qualifiedByName = "instantToOffsetDateTime")
  @Mapping(source = "revisionNumber", target = "revisionNumber", qualifiedByName = "numberToInteger")
  @Mapping(source = "revisionType", target = "revisionType", qualifiedByName = "mapRevisionType")
  @Mapping(source = "revisionInstant", target = "revisionInstant", qualifiedByName = "instantToOffsetDateTime")
  AddressAudit toAuditDto(com.bnm.personservice.entity.Address entity, Number revisionNumber,
      RevisionType revisionType, Instant revisionInstant);

  @Named("numberToInteger")
  default Integer numberToInteger(final Number number) {
    return number != null ? number.intValue() : null;
  }

  @Named("mapRevisionType")
  default AddressAudit.RevisionTypeEnum mapRevisionType(final RevisionType revisionType) {
    return revisionType != null ? AddressAudit.RevisionTypeEnum.fromValue(revisionType.name())
        : null;
  }

  @Named("instantToOffsetDateTime")
  default OffsetDateTime instantToOffsetDateTime(final Instant instant) {
    return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
  }
} 