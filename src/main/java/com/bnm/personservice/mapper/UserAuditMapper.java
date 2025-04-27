package com.bnm.personservice.mapper;

import com.bnm.personservice.entity.User;
import com.bnm.personservice.model.UserAuditDTO;
import org.springframework.stereotype.Component;

@Component
public class UserAuditMapper {

  public UserAuditDTO toDto(final User entity) {
    if (entity == null) {
      return null;
    }

    final UserAuditDTO dto = new UserAuditDTO();
    dto.setId(entity.getId());
    if (entity.getAddress() != null) {
      dto.setAddressId(entity.getAddress().getId());
    }
    dto.setFirstName(entity.getFirstName());
    dto.setLastName(entity.getLastName());
    dto.setStatus(entity.getStatus());
    dto.setVerifiedAt(entity.getVerifiedAt());
    dto.setArchivedAt(entity.getArchivedAt());
    dto.setCreatedAt(entity.getCreatedAt());
    dto.setCreatedBy(entity.getCreatedBy());
    dto.setUpdatedAt(entity.getUpdatedAt());
    dto.setUpdatedBy(entity.getUpdatedBy());
    return dto;
  }
} 