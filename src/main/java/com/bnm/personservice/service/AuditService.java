package com.bnm.personservice.service;

import com.bnm.personservice.entity.Address;
import com.bnm.personservice.entity.Country;
import com.bnm.personservice.entity.Individual;
import com.bnm.personservice.entity.RevInfoEntity;
import com.bnm.personservice.entity.User;
import com.bnm.personservice.mapper.AddressMapper;
import com.bnm.personservice.mapper.CountryMapper;
import com.bnm.personservice.mapper.IndividualMapper;
import com.bnm.personservice.mapper.UserMapper;
import com.bnm.personservice.model.AddressAudit;
import com.bnm.personservice.model.CountryAudit;
import com.bnm.personservice.model.IndividualAudit;
import com.bnm.personservice.model.UserAudit;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  public List<IndividualAudit> getIndividualRevisions(final UUID id) {
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    final List<Number> revisions = auditReader.getRevisions(Individual.class, id);
    log.debug("Found {} revisions for entity Individual with id {}", revisions.size(), id);

    return revisions.stream()
        .map(rev -> {
          final Individual individual = auditReader.find(Individual.class, id, rev);
          final Object[] revisionEntity = (Object[]) auditReader.createQuery()
              .forRevisionsOfEntity(Individual.class, false, true)
              .add(AuditEntity.id().eq(id))
              .add(AuditEntity.revisionNumber().eq(rev))
              .getSingleResult();
          final RevisionType revisionType =
              revisionEntity != null ? (RevisionType) revisionEntity[2] : null;
          final RevInfoEntity revEntity = (RevInfoEntity) revisionEntity[1];
          final Instant revisionInstant = revEntity != null ?
              revEntity.getRevtstmp().toInstant(ZoneOffset.UTC) : null;
          final IndividualAudit dto = individualMapper.toAuditDto(individual, rev, revisionType,
              revisionInstant);
          log.debug("Mapped revision {} for individual {}: {}", rev, id, dto);
          return dto;
        })
        .toList();
  }

  @Transactional(readOnly = true)
  public IndividualAudit getIndividualRevision(final UUID id, final int revision) {
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    log.debug("Getting revision {} for individual {}", revision, id);

    // Проверяем, существует ли такая ревизия
    final List<Number> revisions = auditReader.getRevisions(Individual.class, id);
    final boolean revisionExists = revisions.stream()
        .map(Number::intValue)
        .anyMatch(rev -> rev == revision);

    if (!revisionExists) {
      log.warn("Revision {} not found for individual {}. Available revisions: {}", revision, id,
          revisions);
      return null;
    }

    final Object[] revisionEntity = (Object[]) auditReader.createQuery()
        .forRevisionsOfEntity(Individual.class, false, true)
        .add(AuditEntity.id().eq(id))
        .add(AuditEntity.revisionNumber().eq(revision))
        .getSingleResult();
    final RevisionType revisionType =
        revisionEntity != null ? (RevisionType) revisionEntity[2] : null;
    final RevInfoEntity revEntity = (RevInfoEntity) revisionEntity[1];
    final Instant revisionInstant = revEntity != null ?
        revEntity.getRevtstmp().toInstant(ZoneOffset.UTC) : null;
    final Individual individual = auditReader.find(Individual.class, id, revision);
    if (individual == null) {
      log.warn("Individual not found for id {} and revision {}", id, revision);
      return null;
    }

    final IndividualAudit dto = individualMapper.toAuditDto(individual, revision, revisionType,
        revisionInstant);
    log.debug("Mapped revision {} for individual {}: {}", revision, id, dto);
    return dto;
  }

  @Transactional(readOnly = true)
  public List<UserAudit> getUserRevisions(final UUID id) {
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    final List<Number> revisions = auditReader.getRevisions(User.class, id);
    log.debug("Found {} revisions for entity User with id {}", revisions.size(), id);

    return revisions.stream()
        .map(rev -> {
          final User user = auditReader.find(User.class, id, rev);
          final Object[] revisionEntity = (Object[]) auditReader.createQuery()
              .forRevisionsOfEntity(User.class, false, true)
              .add(AuditEntity.id().eq(id))
              .add(AuditEntity.revisionNumber().eq(rev))
              .getSingleResult();
          final RevisionType revisionType =
              revisionEntity != null ? (RevisionType) revisionEntity[2] : null;
          final RevInfoEntity revEntity = (RevInfoEntity) revisionEntity[1];
          final Instant revisionInstant = revEntity != null ?
              revEntity.getRevtstmp().toInstant(ZoneOffset.UTC) : null;
          final UserAudit dto = userMapper.toAuditDto(user, rev, revisionType, revisionInstant);
          log.debug("Mapped revision {} for user {}: {}", rev, id, dto);
          return dto;
        })
        .toList();
  }

  @Transactional(readOnly = true)
  public UserAudit getUserRevision(final UUID id, final int revision) {
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    log.debug("Getting revision {} for user {}", revision, id);

    // Проверяем, существует ли такая ревизия
    final List<Number> revisions = auditReader.getRevisions(User.class, id);
    final boolean revisionExists = revisions.stream()
        .map(Number::intValue)
        .anyMatch(rev -> rev == revision);

    if (!revisionExists) {
      log.warn("Revision {} not found for user {}. Available revisions: {}", revision, id,
          revisions);
      return null;
    }

    final Object[] revisionEntity = (Object[]) auditReader.createQuery()
        .forRevisionsOfEntity(User.class, false, true)
        .add(AuditEntity.id().eq(id))
        .add(AuditEntity.revisionNumber().eq(revision))
        .getSingleResult();
    final RevisionType revisionType =
        revisionEntity != null ? (RevisionType) revisionEntity[2] : null;
    final RevInfoEntity revEntity = (RevInfoEntity) revisionEntity[1];
    final Instant revisionInstant = revEntity != null ?
        revEntity.getRevtstmp().toInstant(ZoneOffset.UTC) : null;
    final User user = auditReader.find(User.class, id, revision);
    if (user == null) {
      log.warn("User not found for id {} and revision {}", id, revision);
      return null;
    }

    final UserAudit dto = userMapper.toAuditDto(user, revision, revisionType, revisionInstant);
    log.debug("Mapped revision {} for user {}: {}", revision, id, dto);
    return dto;
  }

  @Transactional(readOnly = true)
  public List<AddressAudit> getAddressRevisions(final UUID id) {
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    final List<Number> revisions = auditReader.getRevisions(Address.class, id);
    log.debug("Found {} revisions for address with id {}", revisions.size(), id);

    return revisions.stream()
        .map(rev -> {
          final Address address = auditReader.find(Address.class, id, rev);
          final Object[] revisionEntity = (Object[]) auditReader.createQuery()
              .forRevisionsOfEntity(Address.class, false, true)
              .add(AuditEntity.id().eq(id))
              .add(AuditEntity.revisionNumber().eq(rev))
              .getSingleResult();
          final RevisionType revisionType =
              revisionEntity != null ? (RevisionType) revisionEntity[2] : null;
          final RevInfoEntity revEntity = (RevInfoEntity) revisionEntity[1];
          final Instant revisionInstant = revEntity != null ?
              revEntity.getRevtstmp().toInstant(ZoneOffset.UTC) : null;
          final AddressAudit dto = addressMapper.toAuditDto(address, rev, revisionType,
              revisionInstant);
          log.debug("Mapped revision {} for address {}: {}", rev, id, dto);
          return dto;
        })
        .toList();
  }

  @Transactional(readOnly = true)
  public AddressAudit getAddressRevision(final UUID id, final int revision) {
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    log.debug("Getting revision {} for address {}", revision, id);

    // Проверяем, существует ли такая ревизия
    final List<Number> revisions = auditReader.getRevisions(Address.class, id);
    final boolean revisionExists = revisions.stream()
        .map(Number::intValue)
        .anyMatch(rev -> rev == revision);

    if (!revisionExists) {
      log.warn("Revision {} not found for address {}. Available revisions: {}", revision, id,
          revisions);
      return null;
    }

    final Object[] revisionEntity = (Object[]) auditReader.createQuery()
        .forRevisionsOfEntity(Address.class, false, true)
        .add(AuditEntity.id().eq(id))
        .add(AuditEntity.revisionNumber().eq(revision))
        .getSingleResult();
    final RevisionType revisionType =
        revisionEntity != null ? (RevisionType) revisionEntity[2] : null;
    final RevInfoEntity revEntity = (RevInfoEntity) revisionEntity[1];
    final Instant revisionInstant = revEntity != null ?
        revEntity.getRevtstmp().toInstant(ZoneOffset.UTC) : null;
    final Address address = auditReader.find(Address.class, id, revision);
    if (address == null) {
      log.warn("Address not found for id {} and revision {}", id, revision);
      return null;
    }

    final AddressAudit dto = addressMapper.toAuditDto(address, revision, revisionType,
        revisionInstant);
    log.debug("Mapped revision {} for address {}: {}", revision, id, dto);
    return dto;
  }

  @Transactional(readOnly = true)
  public List<CountryAudit> getCountryRevisions(final Long id) {
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    final List<Number> revisions = auditReader.getRevisions(Country.class, id);
    log.debug("Found {} revisions for country with id {}", revisions.size(), id);

    return revisions.stream()
        .map(rev -> {
          final Country country = auditReader.find(Country.class, id, rev);
          final Object[] revisionEntity = (Object[]) auditReader.createQuery()
              .forRevisionsOfEntity(Country.class, false, true)
              .add(AuditEntity.id().eq(id))
              .add(AuditEntity.revisionNumber().eq(rev))
              .getSingleResult();
          final RevisionType revisionType =
              revisionEntity != null ? (RevisionType) revisionEntity[2] : null;
          final RevInfoEntity revEntity = (RevInfoEntity) revisionEntity[1];
          final Instant revisionInstant = revEntity != null ?
              revEntity.getRevtstmp().toInstant(ZoneOffset.UTC) : null;
          final CountryAudit dto = countryMapper.toAuditDto(country, rev, revisionType,
              revisionInstant);
          log.debug("Mapped revision {} for country {}: {}", rev, id, dto);
          return dto;
        })
        .toList();
  }

  @Transactional(readOnly = true)
  public CountryAudit getCountryRevision(final Long id, final int revision) {
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    log.debug("Getting revision {} for country {}", revision, id);

    // Проверяем, существует ли такая ревизия
    final List<Number> revisions = auditReader.getRevisions(Country.class, id);
    final boolean revisionExists = revisions.stream()
        .map(Number::intValue)
        .anyMatch(rev -> rev == revision);

    if (!revisionExists) {
      log.warn("Revision {} not found for country {}. Available revisions: {}", revision, id,
          revisions);
      return null;
    }

    final Object[] revisionEntity = (Object[]) auditReader.createQuery()
        .forRevisionsOfEntity(Country.class, false, true)
        .add(AuditEntity.id().eq(id))
        .add(AuditEntity.revisionNumber().eq(revision))
        .getSingleResult();
    final RevisionType revisionType =
        revisionEntity != null ? (RevisionType) revisionEntity[2] : null;
    final RevInfoEntity revEntity = (RevInfoEntity) revisionEntity[1];
    final Instant revisionInstant = revEntity != null ?
        revEntity.getRevtstmp().toInstant(ZoneOffset.UTC) : null;
    final Country country = auditReader.find(Country.class, id, revision);
    if (country == null) {
      log.warn("Country not found for id {} and revision {}", id, revision);
      return null;
    }

    final CountryAudit dto = countryMapper.toAuditDto(country, revision, revisionType,
        revisionInstant);
    log.debug("Mapped revision {} for country {}: {}", revision, id, dto);
    return dto;
  }
} 