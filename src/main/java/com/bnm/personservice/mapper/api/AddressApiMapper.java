package com.bnm.personservice.mapper.api;

import com.bnm.personservice.model.AddressAuditResponse;
import com.bnm.personservice.model.AddressRequest;
import com.bnm.personservice.model.AddressResponse;
import com.bnm.personservice.service.domain.Address;
import com.bnm.personservice.service.domain.Country;
import org.hibernate.envers.RevisionType;
import org.mapstruct.BeanMapping;
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

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "domain.id", target = "id")
    @Mapping(source = "revisionNumber", target = "revisionNumber", qualifiedByName = "numberToInteger")
    @Mapping(source = "revisionType", target = "revisionType", qualifiedByName = "mapRevisionType")
    @Mapping(source = "revisionInstant", target = "revisionInstant", qualifiedByName = "instantToOffsetDateTime")
    AddressAuditResponse toAuditResponse(Address domain, Number revisionNumber,
                                         RevisionType revisionType, Instant revisionInstant);

    @Named("numberToInteger")
    default Integer numberToInteger(final Number number) {
        return number != null ? number.intValue() : null;
    }

    @Named("mapRevisionType")
    default AddressAuditResponse.RevisionTypeEnum mapRevisionType(final RevisionType revisionType) {
        return revisionType != null ? AddressAuditResponse.RevisionTypeEnum.fromValue(revisionType.name())
                : null;
    }

    @Named("instantToOffsetDateTime")
    default OffsetDateTime instantToOffsetDateTime(final Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }
} 