package com.bnm.personservice.service;

import com.bnm.personservice.entity.IndividualEntity;
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
    public List<IndividualEntity> findAll() {
        return individualRepository.findAll();
    }

    @Transactional(readOnly = true)
    public IndividualEntity findById(final UUID id) {
        return individualRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Individual not found with id: " + id));
    }

    @Transactional
    public IndividualEntity create(final IndividualEntity individual) {
        return individualRepository.save(individual);
    }

    @Transactional
    public IndividualEntity update(final UUID id, final IndividualEntity updatedIndividual) {
        final IndividualEntity existingIndividual = findById(id);
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