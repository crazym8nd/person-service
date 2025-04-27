package com.bnm.personservice.service;

import com.bnm.personservice.entity.Address;
import com.bnm.personservice.entity.Country;
import com.bnm.personservice.entity.Individual;
import com.bnm.personservice.entity.User;
import com.bnm.personservice.mapper.AddressAuditMapper;
import com.bnm.personservice.mapper.CountryAuditMapper;
import com.bnm.personservice.mapper.IndividualAuditMapper;
import com.bnm.personservice.mapper.UserAuditMapper;
import com.bnm.personservice.model.AddressAuditDTO;
import com.bnm.personservice.model.CountryAuditDTO;
import com.bnm.personservice.model.IndividualAuditDTO;
import com.bnm.personservice.model.UserAuditDTO;
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
  private final UserAuditMapper userAuditMapper;
  private final IndividualAuditMapper individualAuditMapper;
  private final CountryAuditMapper countryAuditMapper;

  @Transactional(readOnly = true)
  public <T> List<T> getRevisions(final Class<T> type, final Object id) {
    if (type.equals(Address.class)) {
      return (List<T>) getAddressRevisions((UUID) id);
    }

    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    final List<Number> revisions = auditReader.getRevisions(type, id);
    log.debug("Found {} revisions for entity {} with id {}", revisions.size(), type.getSimpleName(),
        id);

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
  public List<IndividualAuditDTO> getIndividualRevisions(final UUID id) {
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    final List<Number> revisions = auditReader.getRevisions(Individual.class, id);
    log.debug("Found {} revisions for entity Individual with id {}", revisions.size(), id);

    return revisions.stream()
        .map(rev -> {
          final Individual individual = auditReader.find(Individual.class, id, rev);
          final IndividualAuditDTO dto = individualAuditMapper.toDto(individual);
          log.debug("Mapped revision {} for individual {}: {}", rev, id, dto);
          return dto;
        })
        .toList();
  }

  @Transactional(readOnly = true)
  public IndividualAuditDTO getIndividualRevision(final UUID id, final int revision) {
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

    final Individual individual = auditReader.find(Individual.class, id, revision);
    if (individual == null) {
      log.warn("Individual not found for id {} and revision {}", id, revision);
      return null;
    }

    final IndividualAuditDTO dto = individualAuditMapper.toDto(individual);
    log.debug("Mapped revision {} for individual {}: {}", revision, id, dto);
    return dto;
  }

  @Transactional(readOnly = true)
  public List<UserAuditDTO> getUserRevisions(final UUID id) {
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    final List<Number> revisions = auditReader.getRevisions(User.class, id);
    log.debug("Found {} revisions for entity User with id {}", revisions.size(), id);

    return revisions.stream()
        .map(rev -> {
          final User user = auditReader.find(User.class, id, rev);
          final UserAuditDTO dto = userAuditMapper.toDto(user);
          log.debug("Mapped revision {} for user {}: {}", rev, id, dto);
          return dto;
        })
        .toList();
  }

  @Transactional(readOnly = true)
  public UserAuditDTO getUserRevision(final UUID id, final int revision) {
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

    final User user = auditReader.find(User.class, id, revision);
    if (user == null) {
      log.warn("User not found for id {} and revision {}", id, revision);
      return null;
    }

    final UserAuditDTO dto = userAuditMapper.toDto(user);
    log.debug("Mapped revision {} for user {}: {}", revision, id, dto);
    return dto;
  }

  @Transactional(readOnly = true)
  public List<AddressAuditDTO> getAddressRevisions(final UUID id) {
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    final List<Number> revisions = auditReader.getRevisions(Address.class, id);
    log.debug("Found {} revisions for address with id {}", revisions.size(), id);

    return revisions.stream()
        .map(rev -> {
          final Address address = auditReader.find(Address.class, id, rev);
          final AddressAuditDTO dto = addressAuditMapper.toDto(address);
          log.debug("Mapped revision {} for address {}: {}", rev, id, dto);
          return dto;
        })
        .toList();
  }

  @Transactional(readOnly = true)
  public AddressAuditDTO getAddressRevision(final UUID id, final int revision) {
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

    final Address address = auditReader.find(Address.class, id, revision);
    if (address == null) {
      log.warn("Address not found for id {} and revision {}", id, revision);
      return null;
    }

    final AddressAuditDTO dto = addressAuditMapper.toDto(address);
    log.debug("Mapped revision {} for address {}: {}", revision, id, dto);
    return dto;
  }

  @Transactional(readOnly = true)
  public List<CountryAuditDTO> getCountryRevisions(final Long id) {
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    final List<Number> revisions = auditReader.getRevisions(Country.class, id);
    log.debug("Found {} revisions for country with id {}", revisions.size(), id);

    return revisions.stream()
        .map(rev -> {
          final Country country = auditReader.find(Country.class, id, rev);
          final CountryAuditDTO dto = countryAuditMapper.toDto(country);
          log.debug("Mapped revision {} for country {}: {}", rev, id, dto);
          return dto;
        })
        .toList();
  }

  @Transactional(readOnly = true)
  public CountryAuditDTO getCountryRevision(final Long id, final int revision) {
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

    final Country country = auditReader.find(Country.class, id, revision);
    if (country == null) {
      log.warn("Country not found for id {} and revision {}", id, revision);
      return null;
    }

    final CountryAuditDTO dto = countryAuditMapper.toDto(country);
    log.debug("Mapped revision {} for country {}: {}", revision, id, dto);
    return dto;
  }
} 