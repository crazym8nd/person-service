package com.bnm.personservice.repository;

import com.bnm.personservice.entity.Individual;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndividualRepository extends JpaRepository<Individual, UUID> {

}