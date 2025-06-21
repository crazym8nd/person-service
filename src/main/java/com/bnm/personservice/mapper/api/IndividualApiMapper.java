package com.bnm.personservice.mapper.api;

import com.bnm.personservice.model.IndividualAuditResponse;
import com.bnm.personservice.model.IndividualRequest;
import com.bnm.personservice.model.IndividualResponse;
import com.bnm.personservice.service.domain.Individual;
import com.bnm.personservice.service.domain.User;
import org.hibernate.envers.RevisionType;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface IndividualApiMapper {

    IndividualResponse toResponse(Individual domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "userId", ignore = true)
    Individual toDomain(IndividualRequest request);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "domain.id", target = "id")
    @Mapping(source = "domain.userId", target = "userId")
    @Mapping(source = "domain.passportNumber", target = "passportNumber")
    @Mapping(source = "domain.phoneNumber", target = "phoneNumber")
    @Mapping(source = "domain.email", target = "email")
    @Mapping(source = "revisionNumber", target = "revisionNumber", qualifiedByName = "numberToInteger")
    @Mapping(source = "revisionType", target = "revisionType", qualifiedByName = "mapRevisionType")
    @Mapping(source = "revisionInstant", target = "revisionInstant", qualifiedByName = "instantToOffsetDateTime")
    IndividualAuditResponse toAuditResponse(Individual domain, User user, Number revisionNumber,
                                            RevisionType revisionType, Instant revisionInstant);

    @Named("numberToInteger")
    default Integer numberToInteger(final Number number) {
        return number != null ? number.intValue() : null;
    }

    @Named("mapRevisionType")
    default IndividualAuditResponse.RevisionTypeEnum mapRevisionType(final RevisionType revisionType) {
        return revisionType != null ? IndividualAuditResponse.RevisionTypeEnum.fromValue(revisionType.name())
                : null;
    }

    @Named("instantToOffsetDateTime")
    default OffsetDateTime instantToOffsetDateTime(final Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }
} 