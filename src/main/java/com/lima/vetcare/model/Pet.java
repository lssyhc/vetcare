package com.lima.vetcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @NotNull(message = "Nama hewan peliharaan wajib diisi")
    @Size(min = 1, max = 50, message = "Nama hewan peliharaan harus terdiri dari 1 sampai 50 karakter")
    @Column(nullable = false, length = 50)
    private String name;

    @NotNull(message = "Jenis hewan wajib diisi")
    @Size(min = 1, max = 50, message = "Jenis hewan harus terdiri dari 1 sampai 50 karakter")
    @Column(nullable = false, length = 50)
    private String species;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    public Pet() {
    }

    public Pet(Owner owner, String name, String species) {
        this.owner = owner;
        this.name = name;
        this.species = species;
    }

    public Long getId() {
        return id;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Pet))
            return false;
        Pet pet = (Pet) o;
        return id != null && id.equals(pet.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", species='" + species + '\'' +
                ", owner=" + (owner != null ? owner.getName() : "null") +
                ", appointmentsCount=" + appointments.size() +
                '}';
    }
}