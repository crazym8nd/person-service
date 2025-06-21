package com.bnm.personservice.service;

import com.bnm.personservice.entity.UserEntity;
import com.bnm.personservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public UserEntity findById(final UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Transactional
    public UserEntity create(final UserEntity user) {
        return userRepository.save(user);
    }

    @Transactional
    public UserEntity updateVerificationStatus(final UUID id, final String status) {
        final UserEntity user = findById(id);
        user.setStatus(status);
        user.setVerifiedAt(Instant.now());
        return userRepository.save(user);
    }

    @Transactional
    public UserEntity update(final UUID id, final UserEntity updatedUser) {
        final UserEntity existingUser = findById(id);
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setStatus(updatedUser.getStatus());
        existingUser.setAddress(updatedUser.getAddress());
        return userRepository.save(existingUser);
    }

    @Transactional
    public void delete(final UUID id) {
        userRepository.deleteById(id);
    }
} 