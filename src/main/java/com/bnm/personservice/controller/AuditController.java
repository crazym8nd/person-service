package com.bnm.personservice.controller;

import com.bnm.personservice.entity.Country;
import com.bnm.personservice.entity.Individual;
import com.bnm.personservice.entity.User;
import com.bnm.personservice.service.AuditService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

  private final AuditService auditService;

  @GetMapping("/users/{id}/history")
  public ResponseEntity<List<User>> getUserHistory(@PathVariable final UUID id) {
    return ResponseEntity.ok(auditService.getRevisions(User.class, id));
  }

  @GetMapping("/users/{id}/revision/{rev}")
  public ResponseEntity<User> getUserRevision(@PathVariable final UUID id, @PathVariable final int rev) {
    return ResponseEntity.ok(auditService.getRevision(User.class, id, rev));
  }

  @GetMapping("/individuals/{id}/history")
  public ResponseEntity<List<Individual>> getIndividualHistory(@PathVariable final UUID id) {
    return ResponseEntity.ok(auditService.getRevisions(Individual.class, id));
  }

  @GetMapping("/individuals/{id}/revision/{rev}")
  public ResponseEntity<Individual> getIndividualRevision(@PathVariable final UUID id,
      @PathVariable final int rev) {
    return ResponseEntity.ok(auditService.getRevision(Individual.class, id, rev));
  }

  @GetMapping("/countries/{id}/history")
  public ResponseEntity<List<Country>> getCountryHistory(@PathVariable final Long id) {
    return ResponseEntity.ok(auditService.getRevisions(Country.class, id));
  }

  @GetMapping("/countries/{id}/revision/{rev}")
  public ResponseEntity<Country> getCountryRevision(@PathVariable final Long id, @PathVariable final int rev) {
    return ResponseEntity.ok(auditService.getRevision(Country.class, id, rev));
  }
} 