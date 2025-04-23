package com.bnm.personservice.service;

import com.bnm.personservice.entity.Individual;
import com.bnm.personservice.repository.IndividualRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IndividualService {

  private final IndividualRepository individualRepository;

  @Transactional(readOnly = true)
  public List<Individual> findAll() {
    return individualRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Individual findById(final UUID id) {
    return individualRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Individual not found with id: " + id));
  }

  @Transactional
  public Individual create(final Individual individual) {
    return individualRepository.save(individual);
  }
} 