package com.bnm.personservice.service;

import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {

  private final EntityManager entityManager;

  @Transactional(readOnly = true)
  public <T> List<T> getRevisions(final Class<T> type, final Object id) {
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    final List<Number> revisions = auditReader.getRevisions(type, id);

    return revisions.stream()
        .map(rev -> auditReader.find(type, id, rev))
        .toList();
  }

  @Transactional(readOnly = true)
  public <T> T getRevision(final Class<T> type, final Object id, final int revision) {
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    return auditReader.find(type, id, revision);
  }
} 