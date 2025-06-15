package com.lima.vetcare.service;

import com.lima.vetcare.model.Owner;
import com.lima.vetcare.model.Pet;
import com.lima.vetcare.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    public Pet addPet(String name, String species, Owner owner) {
        Pet pet = new Pet();
        pet.setName(name);
        pet.setSpecies(species);
        pet.setOwner(owner);
        return petRepository.save(pet);
    }

    public List<Pet> getPetsByOwner(Owner owner) {
        return petRepository.findByOwner(owner);
    }

    public Optional<Pet> getPetById(Long petId) {
        return petRepository.findById(petId);
    }

    public Pet updatePet(Long petId, String name, String species) {
        Optional<Pet> optionalPet = petRepository.findById(petId);
        if (optionalPet.isPresent()) {
            Pet pet = optionalPet.get();
            pet.setName(name);
            pet.setSpecies(species);
            return petRepository.save(pet);
        }
        throw new IllegalArgumentException("Hewan peliharaan tidak ditemukan");
    }

    public void deletePet(Long petId) {
        petRepository.deleteById(petId);
    }

    public void verifyPetOwnership(Long petId, Owner owner) {
        Optional<Pet> optionalPet = petRepository.findById(petId);
        if (optionalPet.isEmpty() || !optionalPet.get().getOwner().equals(owner)) {
            throw new SecurityException("Akses tidak sah pada hewan peliharaan");
        }
    }
}
