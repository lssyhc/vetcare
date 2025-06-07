package com.lima.vetcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity untuk tabel HewanPeliharaan
 * Menyimpan data hewan peliharaan milik user
 */
@Entity
@Table(name = "hewan_peliharaan")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Nama hewan tidak boleh kosong")
    @Size(max = 50, message = "Nama hewan maksimal 50 karakter")
    private String nama;

    @Column(nullable = false)
    @NotBlank(message = "Spesies tidak boleh kosong")
    @Size(max = 50, message = "Spesies maksimal 50 karakter")
    private String spesies;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pemilik_id", nullable = false)
    private User pemilik;

    @OneToMany(mappedBy = "hewan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    // Constructors
    public Pet() {
    }

    public Pet(String nama, String spesies, User pemilik) {
        this.nama = nama;
        this.spesies = spesies;
        this.pemilik = pemilik;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getSpesies() {
        return spesies;
    }

    public void setSpesies(String spesies) {
        this.spesies = spesies;
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

    public User getPemilik() {
        return pemilik;
    }

    public void setPemilik(User pemilik) {
        this.pemilik = pemilik;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    // Helper methods
    public String getDisplayName() {
        return nama + " (" + spesies + ")";
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", nama='" + nama + '\'' +
                ", spesies='" + spesies + '\'' +
                ", pemilik=" + (pemilik != null ? pemilik.getNama() : "null") +
                '}';
    }
}
