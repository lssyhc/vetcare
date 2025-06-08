package com.lima.vetcare.repository;

import com.lima.vetcare.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Optional<Owner> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT o FROM Owner o LEFT JOIN FETCH o.pets WHERE o.id = :ownerId")
    Optional<Owner> findByIdWithPets(@Param("ownerId") Long ownerId);
}
