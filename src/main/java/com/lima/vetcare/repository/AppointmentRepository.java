package com.lima.vetcare.repository;

import com.lima.vetcare.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
        List<Appointment> findByOwnerIdOrderByAppointmentTimeDesc(Long ownerId);

        List<Appointment> findByVeterinarianIdOrderByAppointmentTimeAsc(Long veterinarianId);

        List<Appointment> findByVeterinarianIdAndAppointmentTimeBetween(
                        Long veterinarianId,
                        LocalDateTime start,
                        LocalDateTime end);

        @Query("SELECT a FROM Appointment a WHERE a.owner.id = :ownerId " +
                        "AND a.appointmentTime > :currentTime AND a.status = 'SCHEDULED' " +
                        "ORDER BY a.appointmentTime ASC")
        List<Appointment> findUpcomingAppointmentsByOwnerId(
                        @Param("ownerId") Long ownerId,
                        @Param("currentTime") LocalDateTime currentTime);
}
