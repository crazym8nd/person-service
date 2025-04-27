package com.bnm.personservice.service;

import com.bnm.personservice.entity.Address;
import com.bnm.personservice.mapper.AddressAuditMapper;
import com.bnm.personservice.model.AddressAuditDTO;
import com.bnm.personservice.model.RevisionInfo;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

  private final EntityManager entityManager;
  private final AddressAuditMapper addressAuditMapper;

  @Transactional(readOnly = true)
  public RevisionInfo getAvailableRevisions(Class<?> type, Object id) {
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    final List<Number> revisions = auditReader.getRevisions(type, id);
    
    RevisionInfo info = new RevisionInfo();
    info.setAvailableRevisions(revisions.stream().map(Number::intValue).toList());
    info.setEntityId(id.toString());
    info.setEntityType(type.getSimpleName());
    
    log.debug("Found revisions for {} with id {}: {}", type.getSimpleName(), id, revisions);
    return info;
  }

  @Transactional(readOnly = true)
  public <T> List<T> getRevisions(final Class<T> type, final Object id) {
    if (type.equals(Address.class)) {
      return (List<T>) getAddressRevisions((UUID) id);
    }
    
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    final List<Number> revisions = auditReader.getRevisions(type, id);
    log.debug("Found {} revisions for entity {} with id {}", revisions.size(), type.getSimpleName(), id);

    return revisions.stream()
        .map(rev -> auditReader.find(type, id, rev))
        .toList();
  }

  @Transactional(readOnly = true)
  public <T> T getRevision(final Class<T> type, final Object id, final int revision) {
    if (type.equals(Address.class)) {
      return (T) getAddressRevision((UUID) id, revision);
    }
    
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    return auditReader.find(type, id, revision);
  }
  
  @Transactional(readOnly = true)
  public List<AddressAuditDTO> getAddressRevisions(UUID id) {
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    final List<Number> revisions = auditReader.getRevisions(Address.class, id);
    log.debug("Found {} revisions for address with id {}", revisions.size(), id);
    
    return revisions.stream()
        .map(rev -> {
          Address address = auditReader.find(Address.class, id, rev);
          AddressAuditDTO dto = addressAuditMapper.toDto(address);
          log.debug("Mapped revision {} for address {}: {}", rev, id, dto);
          return dto;
        })
        .toList();
  }
  
  @Transactional(readOnly = true)
  public AddressAuditDTO getAddressRevision(UUID id, int revision) {
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    log.debug("Getting revision {} for address {}", revision, id);
    
    // Проверяем, существует ли такая ревизия
    List<Number> revisions = auditReader.getRevisions(Address.class, id);
    if (!revisions.contains(revision)) {
        log.warn("Revision {} not found for address {}. Available revisions: {}", revision, id, revisions);
        return null;
    }
    
    Address address = auditReader.find(Address.class, id, revision);
    if (address == null) {
        log.warn("Address not found for id {} and revision {}", id, revision);
        return null;
    }
    
    AddressAuditDTO dto = addressAuditMapper.toDto(address);
    log.debug("Mapped revision {} for address {}: {}", revision, id, dto);
    return dto;
  }
} 