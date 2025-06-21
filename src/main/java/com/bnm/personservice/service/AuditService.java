package com.bnm.personservice.service;

import com.bnm.personservice.entity.*;
import com.bnm.personservice.mapper.api.AddressApiMapper;
import com.bnm.personservice.mapper.api.CountryApiMapper;
import com.bnm.personservice.mapper.api.IndividualApiMapper;
import com.bnm.personservice.mapper.api.UserApiMapper;
import com.bnm.personservice.mapper.persistence.AddressPersistenceMapper;
import com.bnm.personservice.mapper.persistence.CountryPersistenceMapper;
import com.bnm.personservice.mapper.persistence.IndividualPersistenceMapper;
import com.bnm.personservice.mapper.persistence.UserPersistenceMapper;
import com.bnm.personservice.model.AddressAuditResponse;
import com.bnm.personservice.model.CountryAuditResponse;
import com.bnm.personservice.model.IndividualAuditResponse;
import com.bnm.personservice.model.UserAuditResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final EntityManager entityManager;
    private final AddressApiMapper addressApiMapper;
    private final UserApiMapper userApiMapper;
    private final IndividualApiMapper individualApiMapper;
    private final CountryApiMapper countryApiMapper;
    private final AddressPersistenceMapper addressPersistenceMapper;
    private final UserPersistenceMapper userPersistenceMapper;
    private final IndividualPersistenceMapper individualPersistenceMapper;
    private final CountryPersistenceMapper countryPersistenceMapper;

    @Transactional(readOnly = true)
    public List<IndividualAuditResponse> getIndividualRevisions(final UUID id) {
        final AuditReader auditReader = AuditReaderFactory.get(entityManager);
        final List<Number> revisions = auditReader.getRevisions(IndividualEntity.class, id);
        log.debug("Found {} revisions for entity Individual with id {}", revisions.size(), id);

        return revisions.stream()
                .map(rev -> {
                    final IndividualEntity individualEntity = auditReader.find(IndividualEntity.class, id, rev);
                    final Object[] revisionEntity = (Object[]) auditReader.createQuery()
                            .forRevisionsOfEntity(IndividualEntity.class, false, true)
                            .add(AuditEntity.id().eq(id))
                            .add(AuditEntity.revisionNumber().eq(rev))
                            .getSingleResult();
                    final RevisionType revisionType =
                            revisionEntity != null ? (RevisionType) revisionEntity[2] : null;
                    final RevInfoEntity revEntity = (RevInfoEntity) revisionEntity[1];
                    final Instant revisionInstant = revEntity != null ?
                            revEntity.getRevtstmp().toInstant(ZoneOffset.UTC) : null;
                    final var domain = individualPersistenceMapper.toDomain(individualEntity);
                    final var userDomain = userPersistenceMapper.toDomain(individualEntity.getUser());
                    final IndividualAuditResponse dto = individualApiMapper.toAuditResponse(domain, userDomain, rev, revisionType,
                            revisionInstant);
                    log.debug("Mapped revision {} for individual {}: {}", rev, id, dto);
                    return dto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public IndividualAuditResponse getIndividualRevision(final UUID id, final int revision) {
        final AuditReader auditReader = AuditReaderFactory.get(entityManager);
        log.debug("Getting revision {} for individual {}", revision, id);

        // Проверяем, существует ли такая ревизия
        final List<Number> revisions = auditReader.getRevisions(IndividualEntity.class, id);
        final boolean revisionExists = revisions.stream()
                .map(Number::intValue)
                .anyMatch(rev -> rev == revision);

        if (!revisionExists) {
            log.warn("Revision {} not found for individual {}. Available revisions: {}", revision, id,
                    revisions);
            return null;
        }

        final Object[] revisionEntity = (Object[]) auditReader.createQuery()
                .forRevisionsOfEntity(IndividualEntity.class, false, true)
                .add(AuditEntity.id().eq(id))
                .add(AuditEntity.revisionNumber().eq(revision))
                .getSingleResult();
        final RevisionType revisionType =
                revisionEntity != null ? (RevisionType) revisionEntity[2] : null;
        final RevInfoEntity revEntity = (RevInfoEntity) revisionEntity[1];
        final Instant revisionInstant = revEntity != null ?
                revEntity.getRevtstmp().toInstant(ZoneOffset.UTC) : null;
        final IndividualEntity individual = auditReader.find(IndividualEntity.class, id, revision);
        if (individual == null) {
            log.warn("Individual not found for id {} and revision {}", id, revision);
            return null;
        }

        final var domain = individualPersistenceMapper.toDomain(individual);
        final var userDomain = userPersistenceMapper.toDomain(individual.getUser());
        final IndividualAuditResponse dto = individualApiMapper.toAuditResponse(domain, userDomain, revision, revisionType,
                revisionInstant);
        log.debug("Mapped revision {} for individual {}: {}", revision, id, dto);
        return dto;
    }

    @Transactional(readOnly = true)
    public List<UserAuditResponse> getUserRevisions(final UUID id) {
        final AuditReader auditReader = AuditReaderFactory.get(entityManager);
        final List<Number> revisions = auditReader.getRevisions(UserEntity.class, id);
        log.debug("Found {} revisions for entity User with id {}", revisions.size(), id);

        return revisions.stream()
                .map(rev -> {
                    final UserEntity user = auditReader.find(UserEntity.class, id, rev);
                    final Object[] revisionEntity = (Object[]) auditReader.createQuery()
                            .forRevisionsOfEntity(UserEntity.class, false, true)
                            .add(AuditEntity.id().eq(id))
                            .add(AuditEntity.revisionNumber().eq(rev))
                            .getSingleResult();
                    final RevisionType revisionType =
                            revisionEntity != null ? (RevisionType) revisionEntity[2] : null;
                    final RevInfoEntity revEntity = (RevInfoEntity) revisionEntity[1];
                    final Instant revisionInstant = revEntity != null ?
                            revEntity.getRevtstmp().toInstant(ZoneOffset.UTC) : null;
                    final var domain = userPersistenceMapper.toDomain(user);
                    final UserAuditResponse dto = userApiMapper.toAuditResponse(domain, rev, revisionType, revisionInstant);
                    log.debug("Mapped revision {} for user {}: {}", rev, id, dto);
                    return dto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public UserAuditResponse getUserRevision(final UUID id, final int revision) {
        final AuditReader auditReader = AuditReaderFactory.get(entityManager);
        log.debug("Getting revision {} for user {}", revision, id);

        // Проверяем, существует ли такая ревизия
        final List<Number> revisions = auditReader.getRevisions(UserEntity.class, id);
        final boolean revisionExists = revisions.stream()
                .map(Number::intValue)
                .anyMatch(rev -> rev == revision);

        if (!revisionExists) {
            log.warn("Revision {} not found for user {}. Available revisions: {}", revision, id,
                    revisions);
            return null;
        }

        final Object[] revisionEntity = (Object[]) auditReader.createQuery()
                .forRevisionsOfEntity(UserEntity.class, false, true)
                .add(AuditEntity.id().eq(id))
                .add(AuditEntity.revisionNumber().eq(revision))
                .getSingleResult();
        final RevisionType revisionType =
                revisionEntity != null ? (RevisionType) revisionEntity[2] : null;
        final RevInfoEntity revEntity = (RevInfoEntity) revisionEntity[1];
        final Instant revisionInstant = revEntity != null ?
                revEntity.getRevtstmp().toInstant(ZoneOffset.UTC) : null;
        final UserEntity user = auditReader.find(UserEntity.class, id, revision);
        if (user == null) {
            log.warn("User not found for id {} and revision {}", id, revision);
            return null;
        }

        final var domain = userPersistenceMapper.toDomain(user);
        final UserAuditResponse dto = userApiMapper.toAuditResponse(domain, revision, revisionType, revisionInstant);
        log.debug("Mapped revision {} for user {}: {}", revision, id, dto);
        return dto;
    }

    @Transactional(readOnly = true)
    public List<AddressAuditResponse> getAddressRevisions(final UUID id) {
        final AuditReader auditReader = AuditReaderFactory.get(entityManager);
        final List<Number> revisions = auditReader.getRevisions(AddressEntity.class, id);
        log.debug("Found {} revisions for address with id {}", revisions.size(), id);

        return revisions.stream()
                .map(rev -> {
                    final AddressEntity address = auditReader.find(AddressEntity.class, id, rev);
                    final Object[] revisionEntity = (Object[]) auditReader.createQuery()
                            .forRevisionsOfEntity(AddressEntity.class, false, true)
                            .add(AuditEntity.id().eq(id))
                            .add(AuditEntity.revisionNumber().eq(rev))
                            .getSingleResult();
                    final RevisionType revisionType =
                            revisionEntity != null ? (RevisionType) revisionEntity[2] : null;
                    final RevInfoEntity revEntity = (RevInfoEntity) revisionEntity[1];
                    final Instant revisionInstant = revEntity != null ?
                            revEntity.getRevtstmp().toInstant(ZoneOffset.UTC) : null;
                    final var domain = addressPersistenceMapper.toDomain(address);
                    final AddressAuditResponse dto = addressApiMapper.toAuditResponse(domain, rev, revisionType,
                            revisionInstant);
                    log.debug("Mapped revision {} for address {}: {}", rev, id, dto);
                    return dto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public AddressAuditResponse getAddressRevision(final UUID id, final int revision) {
        final AuditReader auditReader = AuditReaderFactory.get(entityManager);
        log.debug("Getting revision {} for address {}", revision, id);

        // Проверяем, существует ли такая ревизия
        final List<Number> revisions = auditReader.getRevisions(AddressEntity.class, id);
        final boolean revisionExists = revisions.stream()
                .map(Number::intValue)
                .anyMatch(rev -> rev == revision);

        if (!revisionExists) {
            log.warn("Revision {} not found for address {}. Available revisions: {}", revision, id,
                    revisions);
            return null;
        }

        final Object[] revisionEntity = (Object[]) auditReader.createQuery()
                .forRevisionsOfEntity(AddressEntity.class, false, true)
                .add(AuditEntity.id().eq(id))
                .add(AuditEntity.revisionNumber().eq(revision))
                .getSingleResult();
        final RevisionType revisionType =
                revisionEntity != null ? (RevisionType) revisionEntity[2] : null;
        final RevInfoEntity revEntity = (RevInfoEntity) revisionEntity[1];
        final Instant revisionInstant = revEntity != null ?
                revEntity.getRevtstmp().toInstant(ZoneOffset.UTC) : null;
        final AddressEntity address = auditReader.find(AddressEntity.class, id, revision);
        if (address == null) {
            log.warn("Address not found for id {} and revision {}", id, revision);
            return null;
        }

        final var domain = addressPersistenceMapper.toDomain(address);
        final AddressAuditResponse dto = addressApiMapper.toAuditResponse(domain, revision, revisionType,
                revisionInstant);
        log.debug("Mapped revision {} for address {}: {}", revision, id, dto);
        return dto;
    }

    @Transactional(readOnly = true)
    public List<CountryAuditResponse> getCountryRevisions(final Long id) {
        final AuditReader auditReader = AuditReaderFactory.get(entityManager);
        final List<Number> revisions = auditReader.getRevisions(CountryEntity.class, id);
        log.debug("Found {} revisions for country with id {}", revisions.size(), id);

        return revisions.stream()
                .map(rev -> {
                    final CountryEntity country = auditReader.find(CountryEntity.class, id, rev);
                    final Object[] revisionEntity = (Object[]) auditReader.createQuery()
                            .forRevisionsOfEntity(CountryEntity.class, false, true)
                            .add(AuditEntity.id().eq(id))
                            .add(AuditEntity.revisionNumber().eq(rev))
                            .getSingleResult();
                    final RevisionType revisionType =
                            revisionEntity != null ? (RevisionType) revisionEntity[2] : null;
                    final RevInfoEntity revEntity = (RevInfoEntity) revisionEntity[1];
                    final Instant revisionInstant = revEntity != null ?
                            revEntity.getRevtstmp().toInstant(ZoneOffset.UTC) : null;
                    final var domain = countryPersistenceMapper.toDomain(country);
                    final CountryAuditResponse dto = countryApiMapper.toAuditResponse(domain, rev, revisionType,
                            revisionInstant);
                    log.debug("Mapped revision {} for country {}: {}", rev, id, dto);
                    return dto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public CountryAuditResponse getCountryRevision(final Long id, final int revision) {
        final AuditReader auditReader = AuditReaderFactory.get(entityManager);
        log.debug("Getting revision {} for country {}", revision, id);

        // Проверяем, существует ли такая ревизия
        final List<Number> revisions = auditReader.getRevisions(CountryEntity.class, id);
        final boolean revisionExists = revisions.stream()
                .map(Number::intValue)
                .anyMatch(rev -> rev == revision);

        if (!revisionExists) {
            log.warn("Revision {} not found for country {}. Available revisions: {}", revision, id,
                    revisions);
            return null;
        }

        final Object[] revisionEntity = (Object[]) auditReader.createQuery()
                .forRevisionsOfEntity(CountryEntity.class, false, true)
                .add(AuditEntity.id().eq(id))
                .add(AuditEntity.revisionNumber().eq(revision))
                .getSingleResult();
        final RevisionType revisionType =
                revisionEntity != null ? (RevisionType) revisionEntity[2] : null;
        final RevInfoEntity revEntity = (RevInfoEntity) revisionEntity[1];
        final Instant revisionInstant = revEntity != null ?
                revEntity.getRevtstmp().toInstant(ZoneOffset.UTC) : null;
        final CountryEntity country = auditReader.find(CountryEntity.class, id, revision);
        if (country == null) {
            log.warn("Country not found for id {} and revision {}", id, revision);
            return null;
        }

        final var domain = countryPersistenceMapper.toDomain(country);
        final CountryAuditResponse dto = countryApiMapper.toAuditResponse(domain, revision, revisionType,
                revisionInstant);
        log.debug("Mapped revision {} for country {}: {}", revision, id, dto);
        return dto;
    }
} 