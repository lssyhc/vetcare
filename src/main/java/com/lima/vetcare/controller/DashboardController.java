package com.lima.vetcare.controller;

import com.lima.vetcare.model.Owner;
import com.lima.vetcare.model.Pet;
import com.lima.vetcare.model.Veterinarian;
import com.lima.vetcare.service.OwnerService;
import com.lima.vetcare.service.PetService;
import com.lima.vetcare.service.VeterinarianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private VeterinarianService veterinarianService;

    @Autowired
    private PetService petService;

    @GetMapping("/owner")
    public String ownerDashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        Owner owner = ownerService.findOwnerByEmail(email);

        if (owner == null) {
            return "redirect:/auth/login?error=true";
        }

        List<Pet> pets = petService.getPetsByOwner(owner);

        model.addAttribute("owner", owner);
        model.addAttribute("userName", owner.getName());
        model.addAttribute("pets", pets);
        return "dashboard/owner";
    }

    @GetMapping("/vet")
    public String vetDashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        Veterinarian vet = veterinarianService.findVeterinarianByEmail(email);

        if (vet == null) {
            return "redirect:/auth/login?error=true";
        }

        model.addAttribute("veterinarian", vet);
        model.addAttribute("userName", vet.getName());
        model.addAttribute("specialization", vet.getSpecialization());
        return "dashboard/vet";
    }

    @GetMapping
    public String dashboard(Authentication authentication) {
        String userType = authentication.getAuthorities().iterator().next().getAuthority();
        if ("ROLE_OWNER".equals(userType)) {
            return "redirect:/dashboard/owner";
        } else if ("ROLE_VETERINARIAN".equals(userType)) {
            return "redirect:/dashboard/vet";
        }
        return "redirect:/";
    }
}
