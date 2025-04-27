package com.bnm.personservice.controller;

import com.bnm.personservice.api.AuditApi;
import com.bnm.personservice.model.AddressAudit;
import com.bnm.personservice.model.CountryAudit;
import com.bnm.personservice.model.IndividualAudit;
import com.bnm.personservice.model.UserAudit;
import com.bnm.personservice.service.AuditService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuditController implements AuditApi {

  private final AuditService auditService;

  @Override
  public ResponseEntity<List<UserAudit>> getUserHistory(final UUID id) {
    final List<UserAudit> history = auditService.getUserRevisions(id);
    if (history.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(history);
  }

  @Override
  public ResponseEntity<UserAudit> getUserRevision(final UUID id, final Integer rev) {
    final UserAudit revision = auditService.getUserRevision(id, rev);
    if (revision == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(revision);
  }

  @Override
  public ResponseEntity<List<IndividualAudit>> getIndividualHistory(final UUID id) {
    final List<IndividualAudit> history = auditService.getIndividualRevisions(id);
    if (history.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(history);
  }

  @Override
  public ResponseEntity<IndividualAudit> getIndividualRevision(final UUID id, final Integer rev) {
    final IndividualAudit revision = auditService.getIndividualRevision(id, rev);
    if (revision == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(revision);
  }

  @Override
  public ResponseEntity<List<CountryAudit>> getCountryHistory(final Long id) {
    final List<CountryAudit> history = auditService.getCountryRevisions(id);
    if (history.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(history);
  }

  @Override
  public ResponseEntity<CountryAudit> getCountryRevision(final Long id, final Integer rev) {
    final CountryAudit revision = auditService.getCountryRevision(id, rev);
    return revision != null ? ResponseEntity.ok(revision) : ResponseEntity.notFound().build();
  }

  @Override
  public ResponseEntity<List<AddressAudit>> getAddressHistory(final UUID id) {
    final List<AddressAudit> history = auditService.getAddressRevisions(id);
    log.debug("Retrieved {} revisions for address {}", history.size(), id);
    if (history.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(history);
  }

  @Override
  public ResponseEntity<AddressAudit> getAddressRevision(final UUID id, final Integer rev) {
    log.info("Получение ревизии {} для адреса {}", rev, id);
    final AddressAudit revision = auditService.getAddressRevision(id, rev);
    if (revision == null) {
      log.warn("Ревизия {} не найдена для адреса {}", rev, id);
      return ResponseEntity.notFound().build();
    }
    log.info("Успешно получена ревизия {} для адреса {}: {}", rev, id, revision);
    return ResponseEntity.ok(revision);
  }
}