package com.bnm.personservice.mapper.api;

import com.bnm.personservice.model.UserRequest;
import com.bnm.personservice.model.UserResponse;
import com.bnm.personservice.service.domain.Address;
import com.bnm.personservice.service.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface UserApiMapper {

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

    @Named("instantToOffsetDateTime")
    default OffsetDateTime instantToOffsetDateTime(final Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }
} 