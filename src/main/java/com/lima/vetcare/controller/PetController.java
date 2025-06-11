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

import java.util.List;

@Controller
@RequestMapping("/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @Autowired
    private OwnerService ownerService;

    @GetMapping
    public String listPets(Authentication authentication, Model model) {
        // Get current user's email
        String email = authentication.getName();
        
        // Find the owner by email
        Owner owner = ownerService.findOwnerByEmail(email);
        
        if (owner == null) {
            System.out.println("Owner not found for email: " + email);
            model.addAttribute("pets", List.of());
            return "pets/list";
        }
         List<Pet> pets = petService.getPetsByOwner(owner);
        System.out.println("Found " + pets.size() + " pets for owner: " + owner.getName());
        model.addAttribute("pets", pets);
        return "pets/list";
    }

    @GetMapping("/add")
    public String showAddPetForm(Model model) {
        model.addAttribute("pet", new Pet());
        return "pets/add";
    }

    @PostMapping("/add")
    public String addPet(Authentication authentication, @ModelAttribute Pet pet) {
        String email = authentication.getName();
        Owner owner = ownerService.findOwnerByEmail(email);

        petService.addPet(pet.getName(), pet.getSpecies(), owner);
        return "redirect:/pets";
    }

    @GetMapping("/{id}/edit")
    public String showEditPetForm(@PathVariable Long id, Authentication authentication, Model model) {
        String email = authentication.getName();
        Owner owner = ownerService.findOwnerByEmail(email);

        petService.verifyPetOwnership(id, owner);
        Pet pet = petService.getPetById(id).orElseThrow(() -> new IllegalArgumentException("Pet not found"));
        model.addAttribute("pet", pet);
        return "pets/edit";
    }

    @PostMapping("/{id}/edit")
    public String editPet(@PathVariable Long id, Authentication authentication, @ModelAttribute Pet pet) {
        String email = authentication.getName();
        Owner owner = ownerService.findOwnerByEmail(email);
        
        petService.verifyPetOwnership(id, owner);
        petService.updatePet(id, pet.getName(), pet.getSpecies());
        return "redirect:/pets";
    }   
    
    @PostMapping("/{id}/delete")
    public String deletePet(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        Owner owner = ownerService.findOwnerByEmail(email);
        
        if (owner == null) {
            return "redirect:/auth/login?error=true";
        }
        
        try {
            // Verify ownership before deleting
            petService.verifyPetOwnership(id, owner);
            petService.deletePet(id);
            System.out.println("Pet with ID " + id + " deleted successfully by owner: " + owner.getName());
        } catch (SecurityException e) {
            System.err.println("Unauthorized attempt to delete pet with ID " + id + " by user: " + email);
            return "redirect:/pets?error=unauthorized";
        } catch (Exception e) {
            System.err.println("Error deleting pet with ID " + id + ": " + e.getMessage());
            return "redirect:/pets?error=delete_failed";
        }
        
        return "redirect:/pets";
    }
}
