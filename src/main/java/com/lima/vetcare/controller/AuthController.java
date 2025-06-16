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

import jakarta.servlet.http.HttpServletRequest;

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
            HttpServletRequest request,
            Model model) {
        model.addAttribute("requestUri", request.getRequestURI());
        if (error != null) {
            model.addAttribute("error", "Email atau password tidak valid!");
        }
        if (logout != null) {
            model.addAttribute("message", "Anda telah berhasil keluar.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(HttpServletRequest request, Model model) {
        model.addAttribute("requestUri", request.getRequestURI());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegistration(@RequestParam("userType") String userType,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("name") String name,
            @RequestParam(value = "specialization", required = false) String specialization,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (ownerService.existsByEmail(email) || veterinarianService.existsByEmail(email)) {
            model.addAttribute("requestUri", request.getRequestURI());
            model.addAttribute("error", "Email sudah terdaftar!");
            return "auth/register";
        }

        try {
            if ("owner".equals(userType)) {
                ownerService.registerOwner(email, password, name);
                redirectAttributes.addFlashAttribute("message", "Pendaftaran berhasil! Silakan masuk.");
                return "redirect:/auth/login";
            } else if ("veterinarian".equals(userType)) {
                if (specialization == null || specialization.trim().isEmpty()) {
                    model.addAttribute("requestUri", request.getRequestURI());
                    model.addAttribute("error", "Spesialisasi wajib diisi untuk veterinarian!");
                    return "auth/register";
                }
                veterinarianService.registerVeterinarian(email, password, name, specialization);
                redirectAttributes.addFlashAttribute("message", "Pendaftaran berhasil! Silakan masuk.");
                return "redirect:/auth/login";
            } else {
                model.addAttribute("requestUri", request.getRequestURI());
                model.addAttribute("error", "Tipe pengguna tidak valid!");
                return "auth/register";
            }
        } catch (Exception e) {
            model.addAttribute("requestUri", request.getRequestURI());
            model.addAttribute("error", "Pendaftaran gagal: " + e.getMessage());
            return "auth/register";
        }
    }
}
