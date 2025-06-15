package com.lima.vetcare.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lima.vetcare.model.Veterinarian;
import com.lima.vetcare.model.VeterinarianSchedule;
import com.lima.vetcare.service.VeterinarianScheduleService;
import com.lima.vetcare.service.VeterinarianService;

@Controller
@RequestMapping("/schedule")
public class VeterinarianScheduleController {

    private final VeterinarianScheduleService scheduleService;
    private final VeterinarianService veterinarianService;

    public VeterinarianScheduleController(VeterinarianScheduleService scheduleService,
            VeterinarianService veterinarianService) {
        this.scheduleService = scheduleService;
        this.veterinarianService = veterinarianService;
    }

    @GetMapping("/setup")
    public String showScheduleSetup(Model model, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            Veterinarian veterinarian = veterinarianService.findVeterinarianByEmail(email);

            if (veterinarian == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Veterinarian tidak ditemukan");
                return "redirect:/dashboard/vet";
            }

            List<VeterinarianSchedule> weeklySchedule = scheduleService.getWeeklySchedule(veterinarian.getId());

            model.addAttribute("veterinarian", veterinarian);
            model.addAttribute("weeklySchedule", weeklySchedule);

            return "schedule/setup";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kesalahan memuat jadwal: " + e.getMessage());
            return "redirect:/dashboard/vet";
        }
    }

    @PostMapping("/setup")
    public String saveSchedule(@RequestParam("dayOfWeek") Integer dayOfWeek,
            @RequestParam(value = "isActive", required = false, defaultValue = "false") Boolean isActive,
            @RequestParam(value = "startTime", required = false) String startTimeStr,
            @RequestParam(value = "endTime", required = false) String endTimeStr,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            Veterinarian veterinarian = veterinarianService.findVeterinarianByEmail(email);

            if (veterinarian == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Veterinarian tidak ditemukan");
                return "redirect:/schedule/setup";
            }

            LocalTime startTime = null;
            LocalTime endTime = null;

            if (startTimeStr != null && !startTimeStr.trim().isEmpty()) {
                try {
                    startTime = LocalTime.parse(startTimeStr);
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Format waktu mulai tidak valid. Gunakan format JJ:MM");
                    return "redirect:/schedule/setup";
                }
            }

            if (endTimeStr != null && !endTimeStr.trim().isEmpty()) {
                try {
                    endTime = LocalTime.parse(endTimeStr);
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Format waktu selesai tidak valid. Gunakan format JJ:MM");
                    return "redirect:/schedule/setup";
                }
            }

            VeterinarianSchedule savedSchedule = scheduleService.setSchedule(veterinarian.getId(), dayOfWeek, startTime,
                    endTime, isActive);

            String dayName = savedSchedule.getDayName();
            redirectAttributes.addFlashAttribute("successMessage",
                    "Jadwal untuk " + dayName + " berhasil disimpan!");

            return "redirect:/schedule/setup";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/schedule/setup";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Kesalahan menyimpan jadwal: " + e.getMessage());
            return "redirect:/schedule/setup";
        }
    }

    @PostMapping("/setup/save-all")
    public String saveAllSchedules(@RequestParam Map<String, String> allParams,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            Veterinarian veterinarian = veterinarianService.findVeterinarianByEmail(email);

            if (veterinarian == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Veterinarian tidak ditemukan");
                return "redirect:/schedule/setup";
            }

            int savedCount = 0;
            List<String> errors = new ArrayList<>();

            for (int dayOfWeek = 1; dayOfWeek <= 7; dayOfWeek++) {
                try {
                    String isActiveStr = allParams.get("day" + dayOfWeek + "_isActive");
                    Boolean isActive = "true".equals(isActiveStr);
                    String startTimeStr = allParams.get("day" + dayOfWeek + "_startTime");
                    String endTimeStr = allParams.get("day" + dayOfWeek + "_endTime");

                    LocalTime startTime = null;
                    LocalTime endTime = null;

                    if (startTimeStr != null && !startTimeStr.trim().isEmpty()) {
                        try {
                            startTime = LocalTime.parse(startTimeStr);
                        } catch (Exception e) {
                            errors.add("Hari " + dayOfWeek + ": Format waktu mulai tidak valid");
                            continue;
                        }
                    }

                    if (endTimeStr != null && !endTimeStr.trim().isEmpty()) {
                        try {
                            endTime = LocalTime.parse(endTimeStr);
                        } catch (Exception e) {
                            errors.add("Hari " + dayOfWeek + ": Format waktu selesai tidak valid");
                            continue;
                        }
                    }

                    scheduleService.setSchedule(veterinarian.getId(), dayOfWeek, startTime, endTime, isActive);
                    savedCount++;

                } catch (IllegalArgumentException e) {
                    errors.add("Hari " + dayOfWeek + ": " + e.getMessage());
                } catch (Exception e) {
                    errors.add("Hari " + dayOfWeek + ": Kesalahan tidak terduga");
                }
            }

            if (errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Seluruh jadwal selama " + savedCount + " hari berhasil disimpan!");
            } else if (savedCount > 0) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        savedCount + " jadwal tersimpan. Kesalahan: " + String.join("; ", errors));
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Tidak ada jadwal tersimpan. Kesalahan: " + String.join("; ", errors));
            }

            return "redirect:/schedule/setup";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Kesalahan menyimpan jadwal: " + e.getMessage());
            return "redirect:/schedule/setup";
        }
    }

    @GetMapping("/api/schedule/{vetId}/{date}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAvailableTimeSlots(
            @PathVariable Long vetId,
            @PathVariable String date) {
        try {
            LocalDate appointmentDate = LocalDate.parse(date);

            if (appointmentDate.isBefore(LocalDate.now())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Tanggal tidak boleh di masa lalu");
                return ResponseEntity.badRequest().body(response);
            }

            List<LocalTime> availableSlots = scheduleService.getAvailableTimeSlots(vetId, appointmentDate);

            List<String> formattedSlots = availableSlots.stream()
                    .map(time -> time.format(DateTimeFormatter.ofPattern("HH:mm")))
                    .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("availableSlots", formattedSlots);
            response.put("date", date);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Kesalahan terjadi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/api/schedule/{vetId}/check-availability")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkAvailability(
            @PathVariable Long vetId,
            @RequestParam String date,
            @RequestParam String time) {
        try {
            LocalDate appointmentDate = LocalDate.parse(date);
            LocalTime appointmentTime = LocalTime.parse(time);

            LocalDateTime appointmentDateTime = appointmentDate.atTime(appointmentTime);

            boolean isAvailable = scheduleService.isVetAvailable(vetId, appointmentDateTime);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("available", isAvailable);
            response.put("message", isAvailable ? "Slot tersedia" : "Slot tidak tersedia");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Kesalahan terjadi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
