package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.Country;
import com.bnm.personservice.model.CountryAudit;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.hibernate.envers.RevisionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CountryAuditMapper {

  @Mapping(source = "entity.id", target = "id")
  @Mapping(source = "entity.name", target = "name")
  @Mapping(source = "entity.alpha2", target = "alpha2")
  @Mapping(source = "entity.alpha3", target = "alpha3")
  @Mapping(source = "entity.status", target = "status")
  @Mapping(source = "revisionNumber", target = "revisionNumber", qualifiedByName = "numberToInteger")
  @Mapping(source = "revisionType", target = "revisionType", qualifiedByName = "mapRevisionType")
  @Mapping(source = "revisionInstant", target = "revisionInstant", qualifiedByName = "instantToOffsetDateTime")
  CountryAudit toDto(Country entity, Number revisionNumber, RevisionType revisionType,
      Instant revisionInstant);

  @Named("numberToInteger")
  default Integer numberToInteger(final Number number) {
    return number != null ? number.intValue() : null;
  }

  @Named("mapRevisionType")
  default CountryAudit.RevisionTypeEnum mapRevisionType(final RevisionType revisionType) {
    return revisionType != null ? CountryAudit.RevisionTypeEnum.fromValue(revisionType.name())
        : null;
  }

  @Named("instantToOffsetDateTime")
  default OffsetDateTime instantToOffsetDateTime(final Instant instant) {
    return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
  }
} 