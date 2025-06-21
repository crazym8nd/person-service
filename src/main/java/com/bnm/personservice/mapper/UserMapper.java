package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.AddressEntity;
import com.bnm.personservice.entity.UserEntity;
import com.bnm.personservice.model.UserAuditResponse;
import com.bnm.personservice.model.UserRequest;
import com.bnm.personservice.model.UserResponse;
import org.hibernate.envers.RevisionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

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
    UserResponse toResponse(UserEntity entity);

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
    UserEntity toEntity(UserRequest dto, AddressEntity address);

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
    UserAuditResponse toAuditResponse(UserEntity entity, Number revisionNumber,
                                      RevisionType revisionType, Instant revisionInstant);

    @Named("numberToInteger")
    default Integer numberToInteger(final Number number) {
        return number != null ? number.intValue() : null;
    }

    @Named("mapRevisionType")
    default UserAuditResponse.RevisionTypeEnum mapRevisionType(final RevisionType revisionType) {
        return revisionType != null ? UserAuditResponse.RevisionTypeEnum.fromValue(revisionType.name()) : null;
    }

    @Named("instantToOffsetDateTime")
    default OffsetDateTime instantToOffsetDateTime(final Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }
} 