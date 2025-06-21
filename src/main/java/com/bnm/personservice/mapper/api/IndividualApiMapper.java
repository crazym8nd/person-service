package com.bnm.personservice.mapper.api;

import com.bnm.personservice.model.IndividualRequest;
import com.bnm.personservice.model.IndividualResponse;
import com.bnm.personservice.service.domain.Individual;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IndividualApiMapper {

    IndividualResponse toResponse(Individual domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "userId", ignore = true)
    Individual toDomain(IndividualRequest request);
} 