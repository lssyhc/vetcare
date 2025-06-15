package com.lima.vetcare.controller;

import com.lima.vetcare.model.Owner;
import com.lima.vetcare.model.Pet;
import com.lima.vetcare.model.User;
import com.lima.vetcare.model.Veterinarian;
import com.lima.vetcare.service.PetService;
import com.lima.vetcare.service.UserService;
import com.lima.vetcare.service.VeterinarianService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

  @Autowired
  private UserService userService;

  @Autowired
  private VeterinarianService veterinarianService;

  @Autowired
  private PetService petService;

  @GetMapping
  public String showProfile(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User currentUser = userService.findUserByEmail(auth.getName());

    model.addAttribute("user", currentUser);

    if (currentUser instanceof Owner) {
      Owner owner = (Owner) currentUser;
      model.addAttribute("isOwner", true);
      List<Pet> pets = petService.getPetsByOwner(owner);
      model.addAttribute("pets", pets);
      return "dashboard/profile";
    } else if (currentUser instanceof Veterinarian) {
      Veterinarian vet = (Veterinarian) currentUser;
      model.addAttribute("isVeterinarian", true);
      model.addAttribute("specialization", vet.getSpecialization());
      return "dashboard/profile";
    }

    return "redirect:/dashboard";
  }

  @PostMapping("/password")
  public String changePassword(
      @RequestParam("currentPassword") String currentPassword,
      @RequestParam("newPassword") String newPassword,
      @RequestParam("confirmPassword") String confirmPassword,
      RedirectAttributes redirectAttributes) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User currentUser = userService.findUserByEmail(auth.getName());

    if (newPassword.length() < 6) {
      redirectAttributes.addFlashAttribute("passwordError", "Password harus minimal 6 karakter");
      return "redirect:/profile";
    }

    if (!newPassword.equals(confirmPassword)) {
      redirectAttributes.addFlashAttribute("passwordError", "Password baru dan konfirmasi tidak cocok");
      return "redirect:/profile";
    }

    try {
      userService.changePassword(currentUser, currentPassword, newPassword);
      redirectAttributes.addFlashAttribute("passwordSuccess", "Password berhasil diubah");
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("passwordError", e.getMessage());
    }

    return "redirect:/profile";
  }

  @PostMapping("/email")
  public String changeEmail(
      @RequestParam("newEmail") String newEmail,
      RedirectAttributes redirectAttributes) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User currentUser = userService.findUserByEmail(auth.getName());

    if (!newEmail.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
      redirectAttributes.addFlashAttribute("emailError", "Mohon masukkan alamat email yang valid");
      return "redirect:/profile";
    }

    try {
      userService.changeEmail(currentUser, newEmail);
      SecurityContextHolder.getContext().setAuthentication(null);
      redirectAttributes.addFlashAttribute("emailSuccess", "Email berhasil diubah. Silakan login kembali.");
      return "redirect:/login";
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("emailError", e.getMessage());
      return "redirect:/profile";
    }
  }

  @PostMapping("/specialization")
  public String changeSpecialization(
      @RequestParam("specialization") String specialization,
      RedirectAttributes redirectAttributes) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User currentUser = userService.findUserByEmail(auth.getName());

    if (!(currentUser instanceof Veterinarian)) {
      redirectAttributes.addFlashAttribute("error", "Hanya veterinarian yang dapat mengubah spesialisasi");
      return "redirect:/profile";
    }

    try {
      veterinarianService.changeSpecialization((Veterinarian) currentUser, specialization);
      redirectAttributes.addFlashAttribute("specializationSuccess", "Spesialisasi berhasil diperbarui");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("specializationError", "Gagal memperbarui spesialisasi");
    }

    return "redirect:/profile";
  }

  @PostMapping("/delete")
  public String deleteAccount(HttpServletRequest request, RedirectAttributes redirectAttributes) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User currentUser = userService.findUserByEmail(auth.getName());

    try {
      userService.deleteAccount(currentUser);
      request.logout();
      redirectAttributes.addFlashAttribute("message", "Akun Anda telah berhasil dihapus");
      return "redirect:/";
    } catch (ServletException e) {
      redirectAttributes.addFlashAttribute("error", "Gagal menghapus akun: " + e.getMessage());
      return "redirect:/profile";
    }
  }
}