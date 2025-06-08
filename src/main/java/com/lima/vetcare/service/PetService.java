package com.lima.vetcare.service;

import com.lima.vetcare.model.Owner;
import com.lima.vetcare.model.Pet;
import com.lima.vetcare.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Pet addPet(String name, String species, Owner owner) {
        Pet pet = new Pet(owner, name, species);
        return petRepository.save(pet);
    }

    public List<Pet> getPetsByOwner(Owner owner) {
        return petRepository.findByOwner(owner);
    }

    public List<Pet> getPetsByOwnerId(Long ownerId) {
        return petRepository.findByOwnerIdOrderByName(ownerId);
    }

    public Pet updatePet(Long petId, String name, String species) {
        Optional<Pet> petOptional = petRepository.findById(petId);
        if (petOptional.isEmpty()) {
            throw new IllegalArgumentException("Pet not found");
        }

        Pet pet = petOptional.get();
        pet.setName(name);
        pet.setSpecies(species);

        return petRepository.save(pet);
    }

    public void deletePet(Long petId) {
        if (!petRepository.existsById(petId)) {
            throw new IllegalArgumentException("Pet not found");
        }
        petRepository.deleteById(petId);
    }

    public Pet getPetById(Long petId) {
        Optional<Pet> pet = petRepository.findById(petId);
        return pet.orElse(null);
    }

    public boolean verifyPetOwnership(Long petId, Owner owner) {
        return petRepository.existsByIdAndOwnerId(petId, owner.getId());
    }

    public Pet getPetByIdAndOwnerId(Long petId, Long ownerId) {
        Optional<Pet> pet = petRepository.findByIdAndOwnerId(petId, ownerId);
        return pet.orElse(null);
    }

    public long countPetsByOwnerId(Long ownerId) {
        return petRepository.countByOwnerId(ownerId);
    }

    public List<Pet> searchPetsByName(String name) {
        return petRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Pet> getPetsBySpecies(String species) {
        return petRepository.findBySpeciesIgnoreCase(species);
    }
}
