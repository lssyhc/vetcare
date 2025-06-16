package com.lima.vetcare.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
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

import com.lima.vetcare.model.Appointment;
import com.lima.vetcare.model.AppointmentDuration;
import com.lima.vetcare.model.AppointmentStatus;
import com.lima.vetcare.model.Owner;
import com.lima.vetcare.model.Pet;
import com.lima.vetcare.model.User;
import com.lima.vetcare.model.Veterinarian;
import com.lima.vetcare.service.AppointmentService;
import com.lima.vetcare.service.PetService;
import com.lima.vetcare.service.UserService;
import com.lima.vetcare.service.VeterinarianScheduleService;
import com.lima.vetcare.service.VeterinarianService;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

  private final AppointmentService appointmentService;
  private final PetService petService;
  private final VeterinarianService veterinarianService;
  private final VeterinarianScheduleService scheduleService;
  private final UserService userService;

  public AppointmentController(AppointmentService appointmentService,
      PetService petService,
      VeterinarianService veterinarianService,
      VeterinarianScheduleService scheduleService,
      UserService userService) {
    this.appointmentService = appointmentService;
    this.petService = petService;
    this.veterinarianService = veterinarianService;
    this.scheduleService = scheduleService;
    this.userService = userService;
  }

  @GetMapping
  public String listAppointments(Model model, Authentication authentication) {
    String email = authentication.getName();
    List<Appointment> appointments = new ArrayList<>();

    User user = userService.findUserByEmail(email);

    if (user instanceof Owner) {
      Owner owner = (Owner) user;
      appointments = appointmentService.getAppointmentsByOwner(owner);
      model.addAttribute("isOwner", true);
    } else if (user instanceof Veterinarian) {
      Veterinarian vet = (Veterinarian) user;
      appointments = appointmentService.getAppointmentsByVeterinarian(vet);
      model.addAttribute("isVeterinarian", true);
    }

    model.addAttribute("appointments", appointments);
    return "appointments/list";
  }

  @GetMapping("/book")
  public String showBookingForm(Model model, Authentication authentication) {
    String email = authentication.getName();
    User user = userService.findUserByEmail(email);

    if (!(user instanceof Owner)) {
      return "redirect:/dashboard";
    }

    Owner owner = (Owner) user;
    List<Pet> pets = petService.getPetsByOwner(owner);
    List<Veterinarian> vets = appointmentService.getAvailableVeterinarians();

    model.addAttribute("pets", pets);
    model.addAttribute("veterinarians", vets);
    model.addAttribute("today", LocalDate.now());

    return "appointments/book";
  }

  @PostMapping("/book")
  public String bookAppointment(
      @RequestParam("petId") Long petId,
      @RequestParam("veterinarianId") Long veterinarianId,
      @RequestParam("appointmentDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate,
      @RequestParam("appointmentTime") String appointmentTimeStr,
      @RequestParam(value = "durationMinutes", defaultValue = "30") Integer durationMinutes,
      Authentication authentication,
      RedirectAttributes redirectAttributes) {

    try {
      String email = authentication.getName();
      User user = userService.findUserByEmail(email);

      if (!(user instanceof Owner)) {
        return "redirect:/dashboard";
      }

      Owner owner = (Owner) user;
      Pet pet = petService.getPetById(petId)
          .orElseThrow(() -> new RuntimeException("Hewan peliharaan tidak ditemukan dengan id: " + petId));
      Veterinarian veterinarian = veterinarianService.findVeterinarianById(veterinarianId);

      if (!pet.getOwner().getId().equals(owner.getId())) {
        redirectAttributes.addFlashAttribute("errorMessage",
            "Anda hanya dapat membuat janji untuk hewan peliharaan Anda sendiri");
        return "redirect:/appointments/book";
      }

      LocalTime appointmentTime = LocalTime.parse(appointmentTimeStr);
      LocalDateTime appointmentDateTime = appointmentDate.atTime(appointmentTime);

      if (appointmentDateTime.isBefore(LocalDateTime.now())) {
        redirectAttributes.addFlashAttribute("errorMessage",
            "Tidak dapat membuat janji untuk waktu yang sudah berlalu");
        return "redirect:/appointments/book";
      }

      if (!scheduleService.isVetAvailable(veterinarianId, appointmentDateTime)) {
        redirectAttributes.addFlashAttribute("errorMessage",
            "Veterinarian tidak tersedia pada waktu ini. Silakan pilih waktu lain.");
        return "redirect:/appointments/book";
      }

      List<Integer> availableDurations = appointmentService.getAvailableDurations(
          veterinarianId, appointmentDateTime);

      if (!availableDurations.contains(AppointmentDuration.THIRTY_MINUTES.getMinutes())) {
        redirectAttributes.addFlashAttribute("errorMessage",
            "Tidak ada durasi yang tersedia untuk waktu ini. Silakan pilih waktu lain.");
        return "redirect:/appointments/book";
      }

      appointmentService.bookAppointment(owner, veterinarian, pet, appointmentDateTime, durationMinutes);

      redirectAttributes.addFlashAttribute("successMessage",
          "Janji temu berhasil dibuat untuk " + pet.getName() + " pada " +
              appointmentDateTime.format(DateTimeFormatter
                  .ofPattern("dd MMMM yyyy, HH:mm")
                  .withLocale(java.util.Locale.of("id", "ID"))));

      return "redirect:/appointments";

    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan saat membuat janji: " + e.getMessage());
      return "redirect:/appointments/book";
    }
  }

  @GetMapping("/{id}")
  public String viewAppointment(@PathVariable("id") Long appointmentId,
      Model model,
      Authentication authentication) {
    Appointment appointment = appointmentService.getAppointmentById(appointmentId);

    if (appointment == null) {
      return "redirect:/appointments";
    }

    String email = authentication.getName();
    User user = userService.findUserByEmail(email);

    boolean isOwner = user instanceof Owner &&
        appointment.getOwner().getId().equals(((Owner) user).getId());
    boolean isVet = user instanceof Veterinarian &&
        appointment.getVeterinarian().getId().equals(((Veterinarian) user).getId());

    if (!isOwner && !isVet) {
      return "redirect:/appointments";
    }

    model.addAttribute("appointment", appointment);
    model.addAttribute("isOwner", isOwner);
    model.addAttribute("isVeterinarian", isVet);

    return "appointments/details";
  }

  @PostMapping("/{id}/status")
  public String updateAppointmentStatus(
      @PathVariable("id") Long appointmentId,
      @RequestParam("status") String statusStr,
      Authentication authentication,
      RedirectAttributes redirectAttributes) {

    try {
      String email = authentication.getName();
      User user = userService.findUserByEmail(email);

      if (!(user instanceof Veterinarian)) {
        redirectAttributes.addFlashAttribute("errorMessage", "Tidak berwenang");
        return "redirect:/appointments";
      }

      Veterinarian vet = (Veterinarian) user;
      Appointment appointment = appointmentService.getAppointmentById(appointmentId);

      if (appointment == null) {
        redirectAttributes.addFlashAttribute("errorMessage", "Janji temu tidak ditemukan");
        return "redirect:/appointments";
      }

      if (!appointment.getVeterinarian().getId().equals(vet.getId())) {
        redirectAttributes.addFlashAttribute("errorMessage", "Tidak berwenang");
        return "redirect:/appointments";
      }

      AppointmentStatus status = AppointmentStatus.valueOf(statusStr);
      appointmentService.updateAppointmentStatus(appointmentId, status);

      redirectAttributes.addFlashAttribute("successMessage", "Status janji temu berhasil diperbarui");
      return "redirect:/appointments/" + appointmentId;

    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Kesalahan memperbarui status: " + e.getMessage());
      return "redirect:/appointments/" + appointmentId;
    }
  }

  @PostMapping("/{id}/notes")
  public String updateAppointmentNotes(
      @PathVariable("id") Long appointmentId,
      @RequestParam("notes") String notes,
      Authentication authentication,
      RedirectAttributes redirectAttributes) {

    try {
      String email = authentication.getName();
      User user = userService.findUserByEmail(email);

      if (!(user instanceof Veterinarian)) {
        redirectAttributes.addFlashAttribute("errorMessage", "Tidak berwenang");
        return "redirect:/appointments";
      }

      Veterinarian vet = (Veterinarian) user;
      Appointment appointment = appointmentService.getAppointmentById(appointmentId);

      if (appointment == null) {
        redirectAttributes.addFlashAttribute("errorMessage", "Janji temu tidak ditemukan");
        return "redirect:/appointments";
      }

      if (!appointment.getVeterinarian().getId().equals(vet.getId())) {
        redirectAttributes.addFlashAttribute("errorMessage", "Tidak berwenang");
        return "redirect:/appointments";
      }

      appointmentService.addAppointmentNotes(appointmentId, notes);

      redirectAttributes.addFlashAttribute("successMessage", "Catatan janji temu berhasil diperbarui");
      return "redirect:/appointments/" + appointmentId;

    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Kesalahan memperbarui catatan: " + e.getMessage());
      return "redirect:/appointments/" + appointmentId;
    }
  }

  @GetMapping("/api/available-durations")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> getAvailableDurations(
      @RequestParam("veterinarianId") Long veterinarianId,
      @RequestParam("appointmentDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate,
      @RequestParam("appointmentTime") String appointmentTimeStr) {

    Map<String, Object> response = new HashMap<>();

    try {
      LocalTime appointmentTime = LocalTime.parse(appointmentTimeStr);
      LocalDateTime appointmentDateTime = appointmentDate.atTime(appointmentTime);

      List<Integer> availableDurations = appointmentService.getAvailableDurations(
          veterinarianId, appointmentDateTime);

      response.put("success", true);
      response.put("availableDurations", availableDurations);

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      response.put("success", false);
      response.put("message", "Kesalahan: " + e.getMessage());
      return ResponseEntity.badRequest().body(response);
    }
  }
}
