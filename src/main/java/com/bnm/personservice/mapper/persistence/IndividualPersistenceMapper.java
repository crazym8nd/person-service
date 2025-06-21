package com.bnm.personservice.mapper.persistence;

import com.bnm.personservice.repository.entity.IndividualEntity;
import com.bnm.personservice.service.domain.Individual;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IndividualPersistenceMapper {

    @Mapping(target = "userId", source = "user.id")
    Individual toDomain(IndividualEntity entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    IndividualEntity toEntity(Individual domain);
} 