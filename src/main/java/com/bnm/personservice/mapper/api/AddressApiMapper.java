package com.bnm.personservice.mapper.api;

import com.bnm.personservice.model.AddressRequest;
import com.bnm.personservice.model.AddressResponse;
import com.bnm.personservice.service.domain.Address;
import com.bnm.personservice.service.domain.Country;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface AddressApiMapper {

    @Mapping(source = "archived", target = "archived", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(source = "country.id", target = "countryId")
    AddressResponse toResponse(Address domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "archived", ignore = true)
    @Mapping(target = "country", source = "country")
    Address toDomain(AddressRequest request, Country country);

    @Named("instantToOffsetDateTime")
    default OffsetDateTime instantToOffsetDateTime(final Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }
} 