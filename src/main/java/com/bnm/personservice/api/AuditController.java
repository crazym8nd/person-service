package com.bnm.personservice.api;

import com.bnm.personservice.model.AddressAuditResponse;
import com.bnm.personservice.model.CountryAuditResponse;
import com.bnm.personservice.model.IndividualAuditResponse;
import com.bnm.personservice.model.UserAuditResponse;
import com.bnm.personservice.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuditController implements AuditApi {

    private final AuditService auditService;

    @Override
    public ResponseEntity<List<UserAuditResponse>> getUserHistory(final UUID id) {
        final List<UserAuditResponse> history = auditService.getUserRevisions(id);
        if (history.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(history);
    }

    @Override
    public ResponseEntity<UserAuditResponse> getUserRevision(final UUID id, final Integer rev) {
        final UserAuditResponse revision = auditService.getUserRevision(id, rev);
        if (revision == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(revision);
    }

    @Override
    public ResponseEntity<List<IndividualAuditResponse>> getIndividualHistory(final UUID id) {
        final List<IndividualAuditResponse> history = auditService.getIndividualRevisions(id);
        if (history.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(history);
    }

    @Override
    public ResponseEntity<IndividualAuditResponse> getIndividualRevision(final UUID id, final Integer rev) {
        final IndividualAuditResponse revision = auditService.getIndividualRevision(id, rev);
        if (revision == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(revision);
    }

    @Override
    public ResponseEntity<List<CountryAuditResponse>> getCountryHistory(final Integer id) {
        final List<CountryAuditResponse> history = auditService.getCountryRevisions(id.longValue());
        if (history.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(history);
    }

    @Override
    public ResponseEntity<CountryAuditResponse> getCountryRevision(final Integer id, final Integer rev) {
        final CountryAuditResponse revision = auditService.getCountryRevision(id.longValue(), rev);
        return revision != null ? ResponseEntity.ok(revision) : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<AddressAuditResponse>> getAddressHistory(final UUID id) {
        final List<AddressAuditResponse> history = auditService.getAddressRevisions(id);
        log.debug("Retrieved {} revisions for address {}", history.size(), id);
        if (history.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(history);
    }

    @Override
    public ResponseEntity<AddressAuditResponse> getAddressRevision(final UUID id, final Integer rev) {
        log.info("Получение ревизии {} для адреса {}", rev, id);
        final AddressAuditResponse revision = auditService.getAddressRevision(id, rev);
        if (revision == null) {
            log.warn("Ревизия {} не найдена для адреса {}", rev, id);
            return ResponseEntity.notFound().build();
        }
        log.info("Успешно получена ревизия {} для адреса {}: {}", rev, id, revision);
        return ResponseEntity.ok(revision);
    }
}