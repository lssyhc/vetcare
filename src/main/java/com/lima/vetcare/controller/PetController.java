package com.lima.vetcare.controller;

import com.lima.vetcare.model.Owner;
import com.lima.vetcare.model.Pet;
import com.lima.vetcare.service.OwnerService;
import com.lima.vetcare.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @Autowired
    private OwnerService ownerService;

    private Owner getCurrentOwner(Authentication authentication) {
        String email = authentication.getName();
        return ownerService.findOwnerByEmail(email);
    }

    @GetMapping
    public String listPets(Authentication authentication, HttpServletRequest request, Model model) {
        model.addAttribute("requestUri", request.getRequestURI());
        Owner owner = getCurrentOwner(authentication);

        if (owner == null) {
            System.out.println("Pemilik tidak ditemukan untuk email: " + authentication.getName());
            model.addAttribute("pets", List.of());
            return "pets/list";
        }
        List<Pet> pets = petService.getPetsByOwner(owner);
        model.addAttribute("pets", pets);
        return "pets/list";
    }

    @GetMapping("/add")
    public String showAddPetForm(HttpServletRequest request, Model model) {
        model.addAttribute("requestUri", request.getRequestURI());
        model.addAttribute("pet", new Pet());
        return "pets/add";
    }

    @PostMapping("/add")
    public String addPet(Authentication authentication, @ModelAttribute Pet pet) {
        Owner owner = getCurrentOwner(authentication);
        petService.addPet(pet.getName(), pet.getSpecies(), owner);
        return "redirect:/pets";
    }

    @GetMapping("/{id}/edit")
    public String showEditPetForm(@PathVariable Long id, Authentication authentication, HttpServletRequest request,
            Model model) {
        model.addAttribute("requestUri", request.getRequestURI());
        Owner owner = getCurrentOwner(authentication);
        petService.verifyPetOwnership(id, owner);
        Pet pet = petService.getPetById(id)
                .orElseThrow(() -> new IllegalArgumentException());
        model.addAttribute("pet", pet);
        return "pets/edit";
    }

    @PostMapping("/{id}/edit")
    public String editPet(@PathVariable Long id, Authentication authentication, @ModelAttribute Pet pet) {
        Owner owner = getCurrentOwner(authentication);
        petService.verifyPetOwnership(id, owner);
        petService.updatePet(id, pet.getName(), pet.getSpecies());
        return "redirect:/pets";
    }

    @PostMapping("/{id}/delete")
    public String deletePet(@PathVariable Long id, Authentication authentication) {
        Owner owner = getCurrentOwner(authentication);
        try {
            petService.verifyPetOwnership(id, owner);
            petService.deletePet(id);
        } catch (SecurityException e) {
            return "redirect:/pets?error=unauthorized";
        } catch (Exception e) {
            System.err.println("Kesalahan menghapus hewan peliharaan dengan ID " + id + ": " + e.getMessage());
            return "redirect:/pets?error=delete_failed";
        }

        return "redirect:/pets";
    }
}