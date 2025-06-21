package com.bnm.personservice.mapper.persistence;

import com.bnm.personservice.domain.Country;
import com.bnm.personservice.entity.CountryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CountryPersistenceMapper {

    Country toDomain(CountryEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    CountryEntity toEntity(Country domain);
} 