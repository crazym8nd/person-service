package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.Individual;
import com.bnm.personservice.model.IndividualAuditDTO;
import org.springframework.stereotype.Component;

@Component
public class IndividualAuditMapper {

  public IndividualAuditDTO toDto(final Individual entity) {
    if (entity == null) {
      return null;
    }

    final IndividualAuditDTO dto = new IndividualAuditDTO();
    dto.setId(entity.getId());
    if (entity.getUser() != null) {
      dto.setUserId(entity.getUser().getId());
      dto.setFirstName(entity.getUser().getFirstName());
      dto.setLastName(entity.getUser().getLastName());
      dto.setStatus(entity.getUser().getStatus());
      if (entity.getUser().getAddress() != null) {
        dto.setAddressId(entity.getUser().getAddress().getId());
      }
    }
    dto.setPassportNumber(entity.getPassportNumber());
    dto.setPhoneNumber(entity.getPhoneNumber());
    dto.setEmail(entity.getEmail());
    dto.setCreatedAt(entity.getCreatedAt());
    dto.setCreatedBy(entity.getCreatedBy());
    dto.setUpdatedAt(entity.getUpdatedAt());
    dto.setUpdatedBy(entity.getUpdatedBy());
    return dto;
  }
} 