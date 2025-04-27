package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.Address;
import com.bnm.personservice.model.User;
import com.bnm.personservice.model.UserCreate;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

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
  User toDto(com.bnm.personservice.entity.User entity);

  @Named("instantToOffsetDateTime")
  default OffsetDateTime instantToOffsetDateTime(final Instant instant) {
    return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
  }

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
  com.bnm.personservice.entity.User toEntity(UserCreate dto, Address address);
} 