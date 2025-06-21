package com.bnm.personservice.mapper.persistence;

import com.bnm.personservice.repository.entity.AddressEntity;
import com.bnm.personservice.service.domain.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CountryPersistenceMapper.class)
public interface AddressPersistenceMapper {

    Address toDomain(AddressEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    AddressEntity toEntity(Address domain);
} 