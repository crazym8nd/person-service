package com.bnm.personservice.mapper.persistence;

import com.bnm.personservice.domain.User;
import com.bnm.personservice.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AddressPersistenceMapper.class, IndividualPersistenceMapper.class})
public interface UserPersistenceMapper {

    User toDomain(UserEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    UserEntity toEntity(User domain);
} 