package com.lima.vetcare.service;

import com.lima.vetcare.model.Owner;
import com.lima.vetcare.repository.OwnerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final UserService userService;

    public OwnerService(OwnerRepository ownerRepository, UserService userService) {
        this.ownerRepository = ownerRepository;
        this.userService = userService;
    }

    public Owner registerOwner(String email, String password, String name) {
        if (userService.emailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        String encodedPassword = userService.encodePassword(password);
        Owner owner = new Owner(email, encodedPassword, name);

        return ownerRepository.save(owner);
    }

    public Owner findOwnerByEmail(String email) {
        Optional<Owner> owner = ownerRepository.findByEmail(email);
        return owner.orElse(null);
    }

    public Owner getOwnerWithPets(Long ownerId) {
        Optional<Owner> owner = ownerRepository.findByIdWithPets(ownerId);
        return owner.orElse(null);
    }

    public Owner findOwnerById(Long id) {
        Optional<Owner> owner = ownerRepository.findById(id);
        return owner.orElse(null);
    }

    public Owner saveOwner(Owner owner) {
        return ownerRepository.save(owner);
    }

    public boolean existsByEmail(String email) {
        return ownerRepository.existsByEmail(email);
    }
}
