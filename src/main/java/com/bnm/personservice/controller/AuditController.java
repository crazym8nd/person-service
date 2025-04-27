package com.bnm.personservice.controller;

import com.bnm.personservice.model.AddressAuditDTO;
import com.bnm.personservice.model.CountryAuditDTO;
import com.bnm.personservice.model.IndividualAuditDTO;
import com.bnm.personservice.model.UserAuditDTO;
import com.bnm.personservice.service.AuditService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

  private final AuditService auditService;

  @GetMapping("/users/{id}/history")
  public ResponseEntity<List<UserAuditDTO>> getUserHistory(@PathVariable final UUID id) {
    return ResponseEntity.ok(auditService.getUserRevisions(id));
  }

  @GetMapping("/users/{id}/revision/{rev}")
  public ResponseEntity<UserAuditDTO> getUserRevision(@PathVariable final UUID id,
      @PathVariable final int rev) {
    final UserAuditDTO revision = auditService.getUserRevision(id, rev);
    if (revision == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(revision);
  }

  @GetMapping("/individuals/{id}/history")
  public ResponseEntity<List<IndividualAuditDTO>> getIndividualHistory(
      @PathVariable final UUID id) {
    return ResponseEntity.ok(auditService.getIndividualRevisions(id));
  }

  @GetMapping("/individuals/{id}/revision/{rev}")
  public ResponseEntity<IndividualAuditDTO> getIndividualRevision(@PathVariable final UUID id,
      @PathVariable final int rev) {
    final IndividualAuditDTO revision = auditService.getIndividualRevision(id, rev);
    if (revision == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(revision);
  }

  @GetMapping("/countries/{id}/history")
  public ResponseEntity<List<CountryAuditDTO>> getCountryHistory(@PathVariable final Long id) {
    final List<CountryAuditDTO> history = auditService.getCountryRevisions(id);
    return ResponseEntity.ok(history);
  }

  @GetMapping("/countries/{id}/revisions/{rev}")
  public ResponseEntity<CountryAuditDTO> getCountryRevision(
      @PathVariable final Long id,
      @PathVariable final int rev) {
    final CountryAuditDTO revision = auditService.getCountryRevision(id, rev);
    return revision != null ? ResponseEntity.ok(revision) : ResponseEntity.notFound().build();
  }

  @GetMapping("/addresses/{id}/history")
  public ResponseEntity<List<AddressAuditDTO>> getAddressHistory(@PathVariable final UUID id) {
    final List<AddressAuditDTO> history = auditService.getAddressRevisions(id);
    log.debug("Retrieved {} revisions for address {}", history.size(), id);
    return ResponseEntity.ok(history);
  }

  @GetMapping("/addresses/{id}/revision/{rev}")
  public ResponseEntity<AddressAuditDTO> getAddressRevision(
      @PathVariable final UUID id,
      @PathVariable final int rev) {
    log.info("Получение ревизии {} для адреса {}", rev, id);
    final AddressAuditDTO revision = auditService.getAddressRevision(id, rev);
    if (revision == null) {
      log.warn("Ревизия {} не найдена для адреса {}", rev, id);
      return ResponseEntity.notFound().build();
    }
    log.info("Успешно получена ревизия {} для адреса {}: {}", rev, id, revision);
    return ResponseEntity.ok(revision);
  }
}