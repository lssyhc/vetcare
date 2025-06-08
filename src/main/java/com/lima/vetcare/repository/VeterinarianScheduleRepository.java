package com.lima.vetcare.repository;

import com.lima.vetcare.model.VeterinarianSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeterinarianScheduleRepository extends JpaRepository<VeterinarianSchedule, Long> {

    Optional<VeterinarianSchedule> findByVeterinarianIdAndDayOfWeek(Long veterinarianId, Integer dayOfWeek);

    List<VeterinarianSchedule> findByVeterinarianId(Long veterinarianId);

    List<VeterinarianSchedule> findByVeterinarianIdAndIsActive(Long veterinarianId, Boolean isActive);

    @Query("SELECT vs FROM VeterinarianSchedule vs WHERE vs.veterinarian.id = :veterinarianId AND vs.isActive = true ORDER BY vs.dayOfWeek")
    List<VeterinarianSchedule> findActiveSchedulesByVeterinarianIdOrderByDayOfWeek(
            @Param("veterinarianId") Long veterinarianId);

    @Query("SELECT vs FROM VeterinarianSchedule vs WHERE vs.veterinarian.id = :veterinarianId ORDER BY vs.dayOfWeek")
    List<VeterinarianSchedule> findByVeterinarianIdOrderByDayOfWeek(@Param("veterinarianId") Long veterinarianId);

    boolean existsByVeterinarianIdAndIsActive(Long veterinarianId, Boolean isActive);

    void deleteByVeterinarianId(Long veterinarianId);
}
