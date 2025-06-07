package com.lima.vetcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entity untuk tabel VeterinarianAvailability
 * Menyimpan jadwal ketersediaan dokter hewan
 */
@Entity
@Table(name = "veterinarian_availability")
public class VeterinarianAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Tanggal tidak boleh kosong")
    private LocalDate tanggal;

    @Column(name = "jam_mulai", nullable = false)
    @NotNull(message = "Jam mulai tidak boleh kosong")
    private LocalTime jamMulai;

    @Column(name = "jam_selesai", nullable = false)
    @NotNull(message = "Jam selesai tidak boleh kosong")
    private LocalTime jamSelesai;

    @Column(nullable = false)
    private Boolean status = true; // true = aktif, false = non-aktif

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id", nullable = false)
    private User veterinarian;

    // Constructors
    public VeterinarianAvailability() {
    }

    public VeterinarianAvailability(LocalDate tanggal, LocalTime jamMulai, LocalTime jamSelesai, User veterinarian) {
        this.tanggal = tanggal;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
        this.veterinarian = veterinarian;
        this.status = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public LocalTime getJamMulai() {
        return jamMulai;
    }

    public void setJamMulai(LocalTime jamMulai) {
        this.jamMulai = jamMulai;
    }

    public LocalTime getJamSelesai() {
        return jamSelesai;
    }

    public void setJamSelesai(LocalTime jamSelesai) {
        this.jamSelesai = jamSelesai;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public User getVeterinarian() {
        return veterinarian;
    }

    public void setVeterinarian(User veterinarian) {
        this.veterinarian = veterinarian;
    }

    // Helper methods
    public boolean isActive() {
        return status != null && status;
    }

    public String getTimeSlot() {
        return jamMulai + " - " + jamSelesai;
    }

    @Override
    public String toString() {
        return "VeterinarianAvailability{" +
                "id=" + id +
                ", tanggal=" + tanggal +
                ", jamMulai=" + jamMulai +
                ", jamSelesai=" + jamSelesai +
                ", status=" + status +
                ", veterinarian=" + (veterinarian != null ? veterinarian.getNama() : "null") +
                '}';
    }
}
