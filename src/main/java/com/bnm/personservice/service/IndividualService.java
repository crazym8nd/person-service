package com.bnm.personservice.service;

import com.bnm.personservice.domain.Individual;
import com.bnm.personservice.mapper.persistence.IndividualPersistenceMapper;
import com.bnm.personservice.repository.IndividualRepository;
import com.bnm.personservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IndividualService {

    private final IndividualRepository individualRepository;
    private final IndividualPersistenceMapper individualMapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Individual> findAll() {
        return individualRepository.findAll().stream()
                .map(individualMapper::toDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public Individual findById(final UUID id) {
        return individualRepository.findById(id)
                .map(individualMapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Individual not found with id: " + id));
    }

    @Transactional
    public Individual create(final Individual individual, final UUID userId) {
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        individual.setUserId(userId);
        final var entity = individualMapper.toEntity(individual);
        entity.setUser(user);
        final var savedEntity = individualRepository.save(entity);
        return individualMapper.toDomain(savedEntity);
    }

    @Transactional
    public Individual update(final UUID id, final Individual updatedIndividual) {
        final var existingIndividual = individualRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Individual not found with id: " + id));
        existingIndividual.setPassportNumber(updatedIndividual.getPassportNumber());
        existingIndividual.setPhoneNumber(updatedIndividual.getPhoneNumber());
        existingIndividual.setEmail(updatedIndividual.getEmail());
        final var savedEntity = individualRepository.save(existingIndividual);
        return individualMapper.toDomain(savedEntity);
    }

    @Transactional
    public void delete(final UUID id) {
        individualRepository.deleteById(id);
    }
} 