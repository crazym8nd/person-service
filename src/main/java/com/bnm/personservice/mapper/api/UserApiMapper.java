package com.bnm.personservice.mapper.api;

import com.bnm.personservice.domain.Address;
import com.bnm.personservice.domain.User;
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
public interface UserApiMapper {

    @Mapping(source = "createdAt", target = "created", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(source = "updatedAt", target = "updated", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(source = "verifiedAt", target = "verifiedAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(source = "archivedAt", target = "archivedAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(source = "address.id", target = "addressId")
    UserResponse toResponse(User domain);

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
    User toDomain(UserRequest request, Address address);

    @Mapping(source = "domain.id", target = "id")
    @Mapping(source = "domain.firstName", target = "firstName")
    @Mapping(source = "domain.lastName", target = "lastName")
    @Mapping(source = "domain.status", target = "status")
    @Mapping(source = "domain.verifiedAt", target = "verifiedAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(source = "domain.archivedAt", target = "archivedAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(source = "domain.address.id", target = "addressId")
    @Mapping(source = "revisionNumber", target = "revisionNumber", qualifiedByName = "numberToInteger")
    @Mapping(source = "revisionType", target = "revisionType", qualifiedByName = "mapRevisionType")
    @Mapping(source = "revisionInstant", target = "revisionInstant", qualifiedByName = "instantToOffsetDateTime")
    UserAuditResponse toAuditResponse(User domain, Number revisionNumber,
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