package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.IndividualEntity;
import com.bnm.personservice.entity.UserEntity;
import com.bnm.personservice.model.IndividualAuditResponse;
import com.bnm.personservice.model.IndividualRequest;
import com.bnm.personservice.model.IndividualResponse;
import org.hibernate.envers.RevisionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface IndividualMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "createdAt", target = "created", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(source = "updatedAt", target = "updated", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(source = "passportNumber", target = "passportNumber")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "email", target = "email")
    IndividualResponse toResponse(IndividualEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(source = "user", target = "user")
    IndividualEntity toEntity(IndividualRequest dto, UserEntity user);

    @Mapping(source = "entity.id", target = "id")
    @Mapping(source = "entity.user.id", target = "userId")
    @Mapping(source = "entity.passportNumber", target = "passportNumber")
    @Mapping(source = "entity.phoneNumber", target = "phoneNumber")
    @Mapping(source = "entity.email", target = "email")
    @Mapping(source = "revisionNumber", target = "revisionNumber", qualifiedByName = "numberToInteger")
    @Mapping(source = "revisionType", target = "revisionType", qualifiedByName = "mapRevisionType")
    @Mapping(source = "revisionInstant", target = "revisionInstant", qualifiedByName = "instantToOffsetDateTime")
    IndividualAuditResponse toAuditResponse(IndividualEntity entity, Number revisionNumber,
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