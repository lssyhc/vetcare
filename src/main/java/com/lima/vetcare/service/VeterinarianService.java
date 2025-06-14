package com.lima.vetcare.service;

import com.lima.vetcare.model.Veterinarian;
import com.lima.vetcare.repository.VeterinarianRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VeterinarianService {

    private final VeterinarianRepository veterinarianRepository;
    private final UserService userService;

    public VeterinarianService(VeterinarianRepository veterinarianRepository, UserService userService) {
        this.veterinarianRepository = veterinarianRepository;
        this.userService = userService;
    }

    public Veterinarian registerVeterinarian(String email, String password, String name, String specialization) {
        if (userService.emailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        String encodedPassword = userService.encodePassword(password);
        Veterinarian veterinarian = new Veterinarian(email, encodedPassword, name, specialization);

        return veterinarianRepository.save(veterinarian);
    }

    public Veterinarian findVeterinarianByEmail(String email) {
        Optional<Veterinarian> veterinarian = veterinarianRepository.findByEmail(email);
        return veterinarian.orElse(null);
    }

    public List<Veterinarian> getAllVeterinarians() {
        return veterinarianRepository.findAll();
    }

    public List<Veterinarian> getVeterinariansBySpecialization(String specialization) {
        return veterinarianRepository.findBySpecializationIgnoreCase(specialization);
    }

    public Veterinarian findVeterinarianById(Long id) {
        Optional<Veterinarian> veterinarian = veterinarianRepository.findById(id);
        return veterinarian.orElse(null);
    }

    public Veterinarian getVeterinarianWithSchedules(Long veterinarianId) {
        Optional<Veterinarian> veterinarian = veterinarianRepository.findByIdWithSchedules(veterinarianId);
        return veterinarian.orElse(null);
    }

    public List<Veterinarian> getAllVeterinariansWithActiveSchedules() {
        return veterinarianRepository.findAllWithActiveSchedules();
    }

    public Veterinarian saveVeterinarian(Veterinarian veterinarian) {
        return veterinarianRepository.save(veterinarian);
    }

    public boolean existsByEmail(String email) {
        return veterinarianRepository.existsByEmail(email);
    }

    public void changeSpecialization(Veterinarian vet, String specialization) {
        if (specialization == null || specialization.trim().isEmpty()) {
            throw new IllegalArgumentException("Spesialisasi tidak boleh kosong");
        }

        vet.setSpecialization(specialization);
        veterinarianRepository.save(vet);
    }
}
