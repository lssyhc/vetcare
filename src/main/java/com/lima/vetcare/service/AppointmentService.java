package com.lima.vetcare.service;

import com.lima.vetcare.model.*;
import com.lima.vetcare.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
            throw new IllegalArgumentException("Tidak dapat membuat janji temu untuk waktu yang sudah berlalu");
        }

        if (!isValidDuration(durationMinutes)) {
            throw new IllegalArgumentException("Durasi tidak valid. Harus 30, 60, atau 90 menit");
        }

        if (!scheduleService.isSlotAvailable(vet.getId(), appointmentTime, durationMinutes)) {
            throw new IllegalArgumentException("Veterinarian tidak tersedia pada waktu ini");
        }

        List<Appointment> conflicts = appointmentRepository.findByVeterinarianIdAndAppointmentTimeBetween(
                vet.getId(),
                appointmentTime.minusMinutes(90),
                appointmentTime.plusMinutes(durationMinutes + 90));

        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("Slot waktu bertabrakan dengan janji temu yang sudah ada");
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
            throw new IllegalArgumentException("Janji temu tidak ditemukan");
        }

        Appointment appointment = appointmentOptional.get();
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    public Appointment addAppointmentNotes(Long appointmentId, String notes) {
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
        if (appointmentOptional.isEmpty()) {
            throw new IllegalArgumentException("Janji temu tidak ditemukan");
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

    public String formatDateTimeIndonesian(LocalDateTime dateTime) {
        return dateTime.format(
                DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm")
                        .withLocale(new Locale.Builder().setLanguage("id").setRegion("ID").build()));
    }
}
