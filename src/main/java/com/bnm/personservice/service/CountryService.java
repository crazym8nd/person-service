package com.bnm.personservice.service;

import com.bnm.personservice.mapper.persistence.CountryPersistenceMapper;
import com.bnm.personservice.repository.CountryRepository;
import com.bnm.personservice.service.domain.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;
    private final CountryPersistenceMapper countryMapper;

    @Transactional(readOnly = true)
    public List<Country> findAll() {
        return countryRepository.findAll().stream()
                .map(countryMapper::toDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public Country findById(final Long id) {
        return countryRepository.findById(id)
                .map(countryMapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + id));
    }

    @Transactional
    public Country create(final Country country) {
        final var entity = countryMapper.toEntity(country);
        final var savedEntity = countryRepository.save(entity);
        return countryMapper.toDomain(savedEntity);
    }

    @Transactional
    public Country update(final Long id, final Country country) {
        final var existingEntity = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + id));
        existingEntity.setName(country.getName());
        existingEntity.setAlpha2(country.getAlpha2());
        existingEntity.setAlpha3(country.getAlpha3());
        existingEntity.setStatus(country.getStatus());
        final var savedEntity = countryRepository.save(existingEntity);
        return countryMapper.toDomain(savedEntity);
    }

    @Transactional
    public void delete(final Long id) {
        countryRepository.deleteById(id);
    }
} 