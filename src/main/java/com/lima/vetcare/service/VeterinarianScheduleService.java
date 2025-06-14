package com.lima.vetcare.service;

import com.lima.vetcare.model.Veterinarian;
import com.lima.vetcare.model.VeterinarianSchedule;
import com.lima.vetcare.repository.VeterinarianScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VeterinarianScheduleService {

    private final VeterinarianScheduleRepository scheduleRepository;
    private final VeterinarianService veterinarianService;

    public VeterinarianScheduleService(VeterinarianScheduleRepository scheduleRepository,
            VeterinarianService veterinarianService) {
        this.scheduleRepository = scheduleRepository;
        this.veterinarianService = veterinarianService;
    }

    public VeterinarianSchedule setSchedule(Long veterinarianId, Integer dayOfWeek,
            LocalTime startTime, LocalTime endTime, Boolean isActive) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("Day of week must be between 1 (Monday) and 7 (Sunday)");
        }

        if (startTime != null && endTime != null && !startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        if (isActive != null && isActive && (startTime == null || endTime == null)) {
            throw new IllegalArgumentException("Start and end time must be filled when day is active");
        }

        Veterinarian veterinarian = veterinarianService.findVeterinarianById(veterinarianId);
        if (veterinarian == null) {
            throw new IllegalArgumentException("Veterinarian not found");
        }

        Optional<VeterinarianSchedule> existingSchedule = scheduleRepository
                .findByVeterinarianIdAndDayOfWeek(veterinarianId, dayOfWeek);

        VeterinarianSchedule schedule;
        if (existingSchedule.isPresent()) {
            schedule = existingSchedule.get();
        } else {
            schedule = new VeterinarianSchedule();
            schedule.setVeterinarian(veterinarian);
            schedule.setDayOfWeek(dayOfWeek);
        }

        schedule.setIsActive(isActive != null ? isActive : false);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);

        return scheduleRepository.save(schedule);
    }

    public VeterinarianSchedule getScheduleByVetAndDay(Long veterinarianId, Integer dayOfWeek) {
        Optional<VeterinarianSchedule> schedule = scheduleRepository.findByVeterinarianIdAndDayOfWeek(veterinarianId,
                dayOfWeek);
        return schedule.orElse(null);
    }

    public List<VeterinarianSchedule> getWeeklySchedule(Long veterinarianId) {
        return scheduleRepository.findByVeterinarianIdOrderByDayOfWeek(veterinarianId);
    }

    public boolean isVetAvailable(Long veterinarianId, LocalDateTime appointmentTime) {
        int dayOfWeek = appointmentTime.getDayOfWeek().getValue();
        LocalTime appointmentTimeOnly = appointmentTime.toLocalTime();

        VeterinarianSchedule schedule = getScheduleByVetAndDay(veterinarianId, dayOfWeek);

        if (schedule == null || !schedule.getIsActive()) {
            return false;
        }

        LocalTime startTime = schedule.getStartTime();
        LocalTime endTime = schedule.getEndTime();

        if (startTime == null || endTime == null) {
            return false;
        }

        return !appointmentTimeOnly.isBefore(startTime) && appointmentTimeOnly.isBefore(endTime);
    }

    public List<LocalTime> getAvailableTimeSlots(Long veterinarianId, LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        VeterinarianSchedule schedule = getScheduleByVetAndDay(veterinarianId, dayOfWeek);

        List<LocalTime> slots = new ArrayList<>();

        if (schedule == null || !schedule.getIsActive()) {
            return slots;
        }

        LocalTime startTime = schedule.getStartTime();
        LocalTime endTime = schedule.getEndTime();

        if (startTime == null || endTime == null) {
            return slots;
        }

        LocalTime currentSlot = startTime;
        while (currentSlot.isBefore(endTime)) {
            slots.add(currentSlot);
            currentSlot = currentSlot.plusMinutes(30);
        }

        return slots;
    }

    public boolean isSlotAvailable(Long veterinarianId, LocalDateTime startTime, Integer durationMinutes) {
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);

        if (!isVetAvailable(veterinarianId, startTime)) {
            return false;
        }

        int dayOfWeek = startTime.getDayOfWeek().getValue();
        VeterinarianSchedule schedule = getScheduleByVetAndDay(veterinarianId, dayOfWeek);

        if (schedule == null || !schedule.getIsActive()) {
            return false;
        }

        LocalTime workEndTime = schedule.getEndTime();
        if (workEndTime == null) {
            return false;
        }

        LocalTime appointmentEndTime = endTime.toLocalTime();

        return !appointmentEndTime.isAfter(workEndTime);
    }

    public List<VeterinarianSchedule> getActiveSchedules(Long veterinarianId) {
        return scheduleRepository.findByVeterinarianIdAndIsActive(veterinarianId, true);
    }

    public void deleteSchedule(Long veterinarianId, Integer dayOfWeek) {
        Optional<VeterinarianSchedule> schedule = scheduleRepository.findByVeterinarianIdAndDayOfWeek(veterinarianId,
                dayOfWeek);
        schedule.ifPresent(scheduleRepository::delete);
    }

    public void clearAllSchedules(Long veterinarianId) {
        scheduleRepository.deleteByVeterinarianId(veterinarianId);
    }
}
