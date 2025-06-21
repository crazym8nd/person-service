package com.bnm.personservice.mapper.api;

import com.bnm.personservice.model.CountryRequest;
import com.bnm.personservice.model.CountryResponse;
import com.bnm.personservice.service.domain.Country;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CountryApiMapper {

    CountryResponse toResponse(Country domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Country toDomain(CountryRequest request);
} 