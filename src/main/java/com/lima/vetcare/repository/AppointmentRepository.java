package com.lima.vetcare.repository;

import com.lima.vetcare.model.Appointment;
import com.lima.vetcare.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByOwnerId(Long ownerId);

    List<Appointment> findByVeterinarianId(Long veterinarianId);

    List<Appointment> findByAppointmentTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Appointment> findByVeterinarianIdAndAppointmentTimeBetween(Long veterinarianId, LocalDateTime start,
            LocalDateTime end);

    List<Appointment> findByOwnerIdOrderByAppointmentTimeDesc(Long ownerId);

    List<Appointment> findByVeterinarianIdOrderByAppointmentTimeAsc(Long veterinarianId);

    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findByOwnerIdAndStatus(Long ownerId, AppointmentStatus status);

    List<Appointment> findByVeterinarianIdAndStatus(Long veterinarianId, AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.owner.id = :ownerId AND a.appointmentTime > :currentTime AND a.status = 'SCHEDULED' ORDER BY a.appointmentTime ASC")
    List<Appointment> findUpcomingAppointmentsByOwnerId(@Param("ownerId") Long ownerId,
            @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT a FROM Appointment a WHERE a.veterinarian.id = :veterinarianId AND a.appointmentTime BETWEEN :startOfDay AND :endOfDay ORDER BY a.appointmentTime ASC")
    List<Appointment> findTodaysAppointmentsByVeterinarianId(@Param("veterinarianId") Long veterinarianId,
            @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT a FROM Appointment a WHERE a.veterinarian.id = :veterinarianId " +
            "AND a.status = 'SCHEDULED' " +
            "AND (:excludeAppointmentId IS NULL OR a.id != :excludeAppointmentId) " +
            "AND (a.appointmentTime < :appointmentEnd AND " +
            "     (a.appointmentTime + FUNCTION('MINUTE', a.durationMinutes)) > :appointmentStart)")
    List<Appointment> findConflictingAppointments(@Param("veterinarianId") Long veterinarianId,
            @Param("appointmentStart") LocalDateTime appointmentStart,
            @Param("appointmentEnd") LocalDateTime appointmentEnd,
            @Param("excludeAppointmentId") Long excludeAppointmentId);

    Optional<Appointment> findByIdAndOwnerId(Long appointmentId, Long ownerId);

    Optional<Appointment> findByIdAndVeterinarianId(Long appointmentId, Long veterinarianId);
}
