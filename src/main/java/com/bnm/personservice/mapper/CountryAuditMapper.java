package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.Country;
import com.bnm.personservice.model.CountryAuditDTO;
import org.springframework.stereotype.Component;

@Component
public class CountryAuditMapper {

    public CountryAuditDTO toDto(final Country entity) {
        if (entity == null) {
            return null;
        }
        final CountryAuditDTO dto = new CountryAuditDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAlpha2(entity.getAlpha2());
        dto.setAlpha3(entity.getAlpha3());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        return dto;
    }
} 