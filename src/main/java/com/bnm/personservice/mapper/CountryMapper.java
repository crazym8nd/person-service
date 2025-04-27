package com.bnm.personservice.mapper;

import com.bnm.personservice.model.Country;
import com.bnm.personservice.model.CountryCreate;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CountryMapper {

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "alpha2", target = "alpha2")
  @Mapping(source = "alpha3", target = "alpha3")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "createdAt", target = "created", qualifiedByName = "instantToOffsetDateTime")
  @Mapping(source = "updatedAt", target = "updated", qualifiedByName = "instantToOffsetDateTime")
  Country toDto(com.bnm.personservice.entity.Country entity);

  @Named("instantToOffsetDateTime")
  default OffsetDateTime instantToOffsetDateTime(final Instant instant) {
    return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  com.bnm.personservice.entity.Country toEntity(CountryCreate dto);
} 