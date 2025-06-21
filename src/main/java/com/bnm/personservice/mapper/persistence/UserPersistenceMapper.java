package com.bnm.personservice.mapper.persistence;

import com.bnm.personservice.repository.entity.UserEntity;
import com.bnm.personservice.service.domain.User;
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