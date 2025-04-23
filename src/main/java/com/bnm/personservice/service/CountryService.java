package com.bnm.personservice.service;

import com.bnm.personservice.entity.Country;
import com.bnm.personservice.repository.CountryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CountryService {

  private final CountryRepository countryRepository;

  @Transactional(readOnly = true)
  public List<Country> findAll() {
    return countryRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Country findById(final Long id) {
    return countryRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Country not found with id: " + id));
  }

  @Transactional
  public Country create(final Country country) {
    return countryRepository.save(country);
  }

  @Transactional
  public Country update(final Long id, final Country country) {
    final Country existingCountry = findById(id);
    existingCountry.setName(country.getName());
    existingCountry.setAlpha2(country.getAlpha2());
    existingCountry.setAlpha3(country.getAlpha3());
    existingCountry.setStatus(country.getStatus());
    return countryRepository.save(existingCountry);
  }

  @Transactional
  public void delete(final Long id) {
    countryRepository.deleteById(id);
  }
} 