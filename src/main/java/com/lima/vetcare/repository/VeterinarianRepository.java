package com.lima.vetcare.repository;

import com.lima.vetcare.model.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeterinarianRepository extends JpaRepository<Veterinarian, Long> {

    Optional<Veterinarian> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Veterinarian> findBySpecialization(String specialization);

    List<Veterinarian> findBySpecializationIgnoreCase(String specialization);

    @Query("SELECT v FROM Veterinarian v LEFT JOIN FETCH v.schedules WHERE v.id = :veterinarianId")
    Optional<Veterinarian> findByIdWithSchedules(@Param("veterinarianId") Long veterinarianId);

    @Query("SELECT DISTINCT v FROM Veterinarian v LEFT JOIN FETCH v.schedules s WHERE s.isActive = true OR s.isActive IS NULL")
    List<Veterinarian> findAllWithActiveSchedules();
}
