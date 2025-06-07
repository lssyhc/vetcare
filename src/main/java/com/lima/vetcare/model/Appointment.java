package com.lima.vetcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity untuk tabel JanjiTemu
 * Menyimpan data janji temu antara pemilik hewan dan veterinarian
 */
@Entity
@Table(name = "janji_temu")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Waktu janji temu tidak boleh kosong")
    private LocalDateTime waktu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.TERJADWAL;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id", nullable = false)
    private User veterinarian;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hewan_id", nullable = false)
    private Pet hewan;

    @OneToOne(mappedBy = "janjiTemu", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AppointmentResult result;

    // Constructors
    public Appointment() {
    }

    public Appointment(LocalDateTime waktu, User pemilik, User veterinarian, Pet hewan) {
        this.waktu = waktu;
        this.pemilik = pemilik;
        this.veterinarian = veterinarian;
        this.hewan = hewan;
        this.status = AppointmentStatus.TERJADWAL;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getWaktu() {
        return waktu;
    }

    public void setWaktu(LocalDateTime waktu) {
        this.waktu = waktu;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
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

    public User getVeterinarian() {
        return veterinarian;
    }

    public void setVeterinarian(User veterinarian) {
        this.veterinarian = veterinarian;
    }

    public Pet getHewan() {
        return hewan;
    }

    public void setHewan(Pet hewan) {
        this.hewan = hewan;
    }

    public AppointmentResult getResult() {
        return result;
    }

    public void setResult(AppointmentResult result) {
        this.result = result;
    }

    // Helper methods
    public boolean isTerjadwal() {
        return this.status == AppointmentStatus.TERJADWAL;
    }

    public boolean isBatal() {
        return this.status == AppointmentStatus.BATAL;
    }

    public boolean isSelesai() {
        return this.status == AppointmentStatus.SELESAI;
    }

    public boolean hasResult() {
        return this.result != null;
    }

    public boolean canBeCanceled() {
        return this.status == AppointmentStatus.TERJADWAL &&
                this.waktu.isAfter(LocalDateTime.now());
    }

    public boolean canAddResult() {
        return this.status == AppointmentStatus.SELESAI &&
                this.result == null;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", waktu=" + waktu +
                ", status=" + status +
                ", pemilik=" + (pemilik != null ? pemilik.getNama() : "null") +
                ", veterinarian=" + (veterinarian != null ? veterinarian.getNama() : "null") +
                ", hewan=" + (hewan != null ? hewan.getNama() : "null") +
                '}';
    }
}
