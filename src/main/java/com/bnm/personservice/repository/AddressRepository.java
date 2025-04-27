package com.bnm.personservice.repository;

import com.bnm.personservice.entity.Address;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    
    @Query("SELECT a FROM Address a JOIN FETCH a.country WHERE a.id = :id")
    Optional<Address> findByIdWithCountry(UUID id);
    
    @Query("SELECT DISTINCT a FROM Address a JOIN FETCH a.country")
    List<Address> findAllWithCountry();
}