package com.lima.vetcare.repository;

import com.lima.vetcare.model.Owner;
import com.lima.vetcare.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByOwnerId(Long ownerId);

    List<Pet> findByOwner(Owner owner);

    List<Pet> findByOwnerIdOrderByName(Long ownerId);

    List<Pet> findBySpeciesIgnoreCase(String species);

    @Query("SELECT p FROM Pet p WHERE p.id = :petId AND p.owner.id = :ownerId")
    Optional<Pet> findByIdAndOwnerId(@Param("petId") Long petId, @Param("ownerId") Long ownerId);

    long countByOwnerId(Long ownerId);

    List<Pet> findByNameContainingIgnoreCase(String name);

    boolean existsByIdAndOwnerId(Long petId, Long ownerId);
}
