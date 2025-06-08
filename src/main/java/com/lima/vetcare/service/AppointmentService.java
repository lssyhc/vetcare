package com.lima.vetcare.service;

import com.lima.vetcare.model.*;
import com.lima.vetcare.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final VeterinarianScheduleService scheduleService;
    private final VeterinarianService veterinarianService;

    public AppointmentService(AppointmentRepository appointmentRepository,
            VeterinarianScheduleService scheduleService,
            VeterinarianService veterinarianService) {
        this.appointmentRepository = appointmentRepository;
        this.scheduleService = scheduleService;
        this.veterinarianService = veterinarianService;
    }

    public Appointment bookAppointment(Owner owner, Veterinarian vet, Pet pet,
            LocalDateTime appointmentTime, Integer durationMinutes) {
        if (appointmentTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot book appointment in the past");
        }

        if (!isValidDuration(durationMinutes)) {
            throw new IllegalArgumentException("Invalid duration. Must be 30, 60, or 90 minutes");
        }

        if (!scheduleService.isSlotAvailable(vet.getId(), appointmentTime, durationMinutes)) {
            throw new IllegalArgumentException("Veterinarian is not available at this time");
        }

        List<Appointment> conflicts = appointmentRepository.findByVeterinarianIdAndAppointmentTimeBetween(
                vet.getId(),
                appointmentTime.minusMinutes(90),
                appointmentTime.plusMinutes(durationMinutes + 90));

        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("Time slot conflicts with existing appointment");
        }

        Appointment appointment = new Appointment();
        appointment.setOwner(owner);
        appointment.setVeterinarian(vet);
        appointment.setPet(pet);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setDurationMinutes(durationMinutes);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAppointmentsByOwner(Owner owner) {
        return appointmentRepository.findByOwnerIdOrderByAppointmentTimeDesc(owner.getId());
    }

    public List<Appointment> getAppointmentsByVeterinarian(Veterinarian vet) {
        return appointmentRepository.findByVeterinarianIdOrderByAppointmentTimeAsc(vet.getId());
    }

    public List<Appointment> getTodaysAppointments(Veterinarian vet) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        return appointmentRepository.findByVeterinarianIdAndAppointmentTimeBetween(
                vet.getId(), startOfDay, endOfDay);
    }

    public Appointment updateAppointmentStatus(Long appointmentId, AppointmentStatus status) {
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
        if (appointmentOptional.isEmpty()) {
            throw new IllegalArgumentException("Appointment not found");
        }

        Appointment appointment = appointmentOptional.get();
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    public Appointment addAppointmentNotes(Long appointmentId, String notes) {
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
        if (appointmentOptional.isEmpty()) {
            throw new IllegalArgumentException("Appointment not found");
        }

        Appointment appointment = appointmentOptional.get();
        appointment.setNotes(notes);
        return appointmentRepository.save(appointment);
    }

    public List<Veterinarian> getAvailableVeterinarians() {
        return veterinarianService.getAllVeterinariansWithActiveSchedules();
    }

    public List<Integer> getAvailableDurations(Long veterinarianId, LocalDateTime appointmentTime) {
        int dayOfWeek = appointmentTime.getDayOfWeek().getValue();
        VeterinarianSchedule schedule = scheduleService.getScheduleByVetAndDay(veterinarianId, dayOfWeek);

        if (schedule == null || !schedule.getIsActive() || schedule.getEndTime() == null) {
            return List.of();
        }

        LocalDateTime workEndTime = appointmentTime.toLocalDate().atTime(schedule.getEndTime());
        long remainingMinutes = java.time.Duration.between(appointmentTime, workEndTime).toMinutes();

        List<Integer> standardDurations = Arrays.asList(30, 60, 90);

        return standardDurations.stream()
                .filter(duration -> duration <= remainingMinutes)
                .collect(Collectors.toList());
    }

    public List<Appointment> getUpcomingAppointments(Long ownerId) {
        return appointmentRepository.findUpcomingAppointmentsByOwnerId(ownerId, LocalDateTime.now());
    }

    public Appointment getAppointmentById(Long appointmentId) {
        Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);
        return appointment.orElse(null);
    }

    private boolean isValidDuration(Integer durationMinutes) {
        return durationMinutes != null &&
                (durationMinutes == 30 || durationMinutes == 60 || durationMinutes == 90);
    }

    public Appointment cancelAppointment(Long appointmentId) {
        return updateAppointmentStatus(appointmentId, AppointmentStatus.CANCELLED);
    }

    public Appointment completeAppointment(Long appointmentId) {
        return updateAppointmentStatus(appointmentId, AppointmentStatus.COMPLETED);
    }
}
