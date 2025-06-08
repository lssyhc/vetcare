package com.lima.vetcare.controller;

import com.lima.vetcare.service.OwnerService;
import com.lima.vetcare.service.VeterinarianService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final OwnerService ownerService;
    private final VeterinarianService veterinarianService;

    public AuthController(OwnerService ownerService, VeterinarianService veterinarianService) {
        this.ownerService = ownerService;
        this.veterinarianService = veterinarianService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password!");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegistration(@RequestParam("userType") String userType,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("name") String name,
            @RequestParam(value = "specialization", required = false) String specialization,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (ownerService.existsByEmail(email) || veterinarianService.existsByEmail(email)) {
            model.addAttribute("error", "Email already exists!");
            return "auth/register";
        }

        try {
            if ("owner".equals(userType)) {
                ownerService.registerOwner(email, password, name);
                redirectAttributes.addFlashAttribute("message", "Registration successful! Please login.");
                return "redirect:/auth/login";
            } else if ("veterinarian".equals(userType)) {
                if (specialization == null || specialization.trim().isEmpty()) {
                    model.addAttribute("error", "Specialization is required for veterinarians!");
                    return "auth/register";
                }
                veterinarianService.registerVeterinarian(email, password, name, specialization);
                redirectAttributes.addFlashAttribute("message", "Registration successful! Please login.");
                return "redirect:/auth/login";
            } else {
                model.addAttribute("error", "Invalid user type!");
                return "auth/register";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "auth/register";
        }
    }
}
