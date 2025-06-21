package com.bnm.personservice.service;

import com.bnm.personservice.domain.User;
import com.bnm.personservice.mapper.persistence.UserPersistenceMapper;
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
    private final UserPersistenceMapper userMapper;

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public User findById(final UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDomain)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Transactional
    public User create(final User user) {
        final var entity = userMapper.toEntity(user);
        final var savedEntity = userRepository.save(entity);
        return userMapper.toDomain(savedEntity);
    }

    @Transactional
    public User updateVerificationStatus(final UUID id, final String status) {
        final var userEntity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userEntity.setStatus(status);
        userEntity.setVerifiedAt(Instant.now());
        final var savedEntity = userRepository.save(userEntity);
        return userMapper.toDomain(savedEntity);
    }

    @Transactional
    public User update(final UUID id, final User updatedUser) {
        final var existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setStatus(updatedUser.getStatus());
        // Маппинг для адреса должен быть обработан отдельно, если нужно
        // existingUser.setAddress(userMapper.toEntity(updatedUser.getAddress()));
        final var savedEntity = userRepository.save(existingUser);
        return userMapper.toDomain(savedEntity);
    }

    @Transactional
    public void delete(final UUID id) {
        userRepository.deleteById(id);
    }
} 