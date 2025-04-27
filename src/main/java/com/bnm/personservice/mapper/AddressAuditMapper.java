package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.Address;
import com.bnm.personservice.model.AddressAuditDTO;
import org.springframework.stereotype.Component;

@Component
public class AddressAuditMapper {

  public AddressAuditDTO toDto(final Address entity) {
    if (entity == null) {
      return null;
    }

    final AddressAuditDTO dto = new AddressAuditDTO();
    dto.setId(entity.getId());
    if (entity.getCountry() != null) {
      dto.setCountryId(entity.getCountry().getId());
      dto.setCountryName(entity.getCountry().getName());
    }
    dto.setAddress(entity.getAddress());
    dto.setZipCode(entity.getZipCode());
    dto.setCity(entity.getCity());
    dto.setState(entity.getState());
    dto.setArchived(entity.getArchived());
    dto.setCreatedAt(entity.getCreatedAt());
    dto.setCreatedBy(entity.getCreatedBy());
    dto.setUpdatedAt(entity.getUpdatedAt());
    dto.setUpdatedBy(entity.getUpdatedBy());
    return dto;
  }
} 