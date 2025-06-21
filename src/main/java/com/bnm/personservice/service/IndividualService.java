package com.bnm.personservice.service;

import com.bnm.personservice.entity.Individual;
import com.bnm.personservice.repository.IndividualRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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

    @Transactional
    public Individual update(final UUID id, final Individual updatedIndividual) {
        final Individual existingIndividual = findById(id);
        existingIndividual.setUser(updatedIndividual.getUser());
        existingIndividual.setPassportNumber(updatedIndividual.getPassportNumber());
        existingIndividual.setPhoneNumber(updatedIndividual.getPhoneNumber());
        existingIndividual.setEmail(updatedIndividual.getEmail());
        return individualRepository.save(existingIndividual);
    }

    @Transactional
    public void delete(final UUID id) {
        individualRepository.deleteById(id);
    }
} 