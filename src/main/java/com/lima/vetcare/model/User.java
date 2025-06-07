package com.lima.vetcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity untuk tabel Pengguna
 * Menyimpan data user (Pemilik Hewan, Veterinarian, Admin)
 */
@Entity
@Table(name = "pengguna")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Email(message = "Format email tidak valid")
    @NotBlank(message = "Email tidak boleh kosong")
    private String email;

    @Column(name = "password_hash", nullable = false)
    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 6, message = "Password minimal 6 karakter")
    private String passwordHash;

    @Column(nullable = false)
    @NotBlank(message = "Nama tidak boleh kosong")
    @Size(max = 100, message = "Nama maksimal 100 karakter")
    private String nama;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(length = 100)
    @Size(max = 100, message = "Spesialisasi maksimal 100 karakter")
    private String spesialisasi; // nullable, untuk veterinarian

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "pemilik", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pet> pets = new ArrayList<>();

    @OneToMany(mappedBy = "pemilik", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> ownedAppointments = new ArrayList<>();

    @OneToMany(mappedBy = "veterinarian", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> veterinarianAppointments = new ArrayList<>();

    @OneToMany(mappedBy = "veterinarian", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VeterinarianAvailability> availabilities = new ArrayList<>();

    // Constructors
    public User() {
    }

    public User(String email, String passwordHash, String nama, Role role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.nama = nama;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getSpesialisasi() {
        return spesialisasi;
    }

    public void setSpesialisasi(String spesialisasi) {
        this.spesialisasi = spesialisasi;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }

    public List<Appointment> getOwnedAppointments() {
        return ownedAppointments;
    }

    public void setOwnedAppointments(List<Appointment> ownedAppointments) {
        this.ownedAppointments = ownedAppointments;
    }

    public List<Appointment> getVeterinarianAppointments() {
        return veterinarianAppointments;
    }

    public void setVeterinarianAppointments(List<Appointment> veterinarianAppointments) {
        this.veterinarianAppointments = veterinarianAppointments;
    }

    public List<VeterinarianAvailability> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(List<VeterinarianAvailability> availabilities) {
        this.availabilities = availabilities;
    }

    // Helper methods
    public boolean isVeterinarian() {
        return this.role == Role.VETERINARIAN;
    }

    public boolean isPemilik() {
        return this.role == Role.PEMILIK;
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nama='" + nama + '\'' +
                ", role=" + role +
                ", spesialisasi='" + spesialisasi + '\'' +
                ", emailVerified=" + emailVerified +
                '}';
    }
}
