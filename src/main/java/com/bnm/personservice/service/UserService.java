package com.bnm.personservice.service;

import com.bnm.personservice.entity.User;
import com.bnm.personservice.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public List<User> findAll() {
    return userRepository.findAll();
  }

  @Transactional(readOnly = true)
  public User findById(final UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
  }

  @Transactional
  public User create(final User user) {
    user.setVerifiedAt(Instant.now());
    user.setArchivedAt(Instant.now());
    return userRepository.save(user);
  }

  @Transactional
  public User updateVerificationStatus(final UUID id, final String status) {
    final User user = findById(id);
    user.setStatus(status);
    user.setVerifiedAt(Instant.now());
    return userRepository.save(user);
  }

  @Transactional
  public User update(final UUID id, final User updatedUser) {
    final User existingUser = findById(id);
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