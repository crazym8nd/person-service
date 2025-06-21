package com.bnm.personservice.repository;

import com.bnm.personservice.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, UUID> {

    @Query("SELECT a FROM AddressEntity a JOIN FETCH a.country WHERE a.id = :id")
    Optional<AddressEntity> findByIdWithCountry(UUID id);

    @Query("SELECT DISTINCT a FROM AddressEntity a JOIN FETCH a.country")
    List<AddressEntity> findAllWithCountry();
}