package com.bnm.personservice.mapper.api;

import com.bnm.personservice.model.CountryAuditResponse;
import com.bnm.personservice.model.CountryRequest;
import com.bnm.personservice.model.CountryResponse;
import com.bnm.personservice.service.domain.Country;
import org.hibernate.envers.RevisionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface CountryApiMapper {

    @Mapping(source = "createdAt", target = "created", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(source = "updatedAt", target = "updated", qualifiedByName = "instantToOffsetDateTime")
    CountryResponse toResponse(Country domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Country toDomain(CountryRequest request);


    @Mapping(source = "domain.id", target = "id")
    @Mapping(source = "domain.name", target = "name")
    @Mapping(source = "domain.alpha2", target = "alpha2")
    @Mapping(source = "domain.alpha3", target = "alpha3")
    @Mapping(source = "domain.status", target = "status")
    @Mapping(source = "revisionNumber", target = "revisionNumber", qualifiedByName = "numberToInteger")
    @Mapping(source = "revisionType", target = "revisionType", qualifiedByName = "mapRevisionType")
    @Mapping(source = "revisionInstant", target = "revisionInstant", qualifiedByName = "instantToOffsetDateTime")
    CountryAuditResponse toAuditResponse(Country domain, Number revisionNumber,
                                         RevisionType revisionType, Instant revisionInstant);

    @Named("numberToInteger")
    default Integer numberToInteger(final Number number) {
        return number != null ? number.intValue() : null;
    }

    @Named("mapRevisionType")
    default CountryAuditResponse.RevisionTypeEnum mapRevisionType(final RevisionType revisionType) {
        return revisionType != null ? CountryAuditResponse.RevisionTypeEnum.fromValue(revisionType.name())
                : null;
    }

    @Named("instantToOffsetDateTime")
    default OffsetDateTime instantToOffsetDateTime(final Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }
} 