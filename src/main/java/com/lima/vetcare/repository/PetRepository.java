package com.lima.vetcare.repository;

import com.lima.vetcare.model.Owner;
import com.lima.vetcare.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByOwner(Owner owner);
}
