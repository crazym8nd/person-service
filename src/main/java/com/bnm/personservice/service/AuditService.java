package com.bnm.personservice.service;

import com.bnm.personservice.entity.*;
import com.bnm.personservice.mapper.AddressMapper;
import com.bnm.personservice.mapper.CountryMapper;
import com.bnm.personservice.mapper.IndividualMapper;
import com.bnm.personservice.mapper.UserMapper;
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
    private final AddressMapper addressMapper;
    private final UserMapper userMapper;
    private final IndividualMapper individualMapper;
    private final CountryMapper countryMapper;

    @Transactional(readOnly = true)
    public List<IndividualAuditResponse> getIndividualRevisions(final UUID id) {
        final AuditReader auditReader = AuditReaderFactory.get(entityManager);
        final List<Number> revisions = auditReader.getRevisions(IndividualEntity.class, id);
        log.debug("Found {} revisions for entity Individual with id {}", revisions.size(), id);

        return revisions.stream()
                .map(rev -> {
                    final IndividualEntity individual = auditReader.find(IndividualEntity.class, id, rev);
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
                    final IndividualAuditResponse dto = individualMapper.toAuditResponse(individual, rev, revisionType,
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

        final IndividualAuditResponse dto = individualMapper.toAuditResponse(individual, revision, revisionType,
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
                    final UserAuditResponse dto = userMapper.toAuditResponse(user, rev, revisionType, revisionInstant);
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

        final UserAuditResponse dto = userMapper.toAuditResponse(user, revision, revisionType, revisionInstant);
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
                    final AddressAuditResponse dto = addressMapper.toAuditResponse(address, rev, revisionType,
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

        final AddressAuditResponse dto = addressMapper.toAuditResponse(address, revision, revisionType,
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
                    final CountryAuditResponse dto = countryMapper.toAuditResponse(country, rev, revisionType,
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

        final CountryAuditResponse dto = countryMapper.toAuditResponse(country, revision, revisionType,
                revisionInstant);
        log.debug("Mapped revision {} for country {}: {}", revision, id, dto);
        return dto;
    }
} 