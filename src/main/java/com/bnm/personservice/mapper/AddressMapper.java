package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.Country;
import com.bnm.personservice.model.Address;
import com.bnm.personservice.model.AddressCreate;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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

  @Named("instantToOffsetDateTime")
  default OffsetDateTime instantToOffsetDateTime(final Instant instant) {
    return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "archived", ignore = true)
  @Mapping(source = "country", target = "country")
  com.bnm.personservice.entity.Address toEntity(AddressCreate dto, Country country);
} 