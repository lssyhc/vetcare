package com.lima.vetcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity untuk tabel HasilTemu
 * Menyimpan hasil pemeriksaan dari janji temu yang telah selesai
 */
@Entity
@Table(name = "hasil_temu")
public class AppointmentResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Diagnosis tidak boleh kosong")
    @Size(max = 5000, message = "Diagnosis maksimal 5000 karakter")
    private String diagnosis;

    @Column(name = "rencana_perawatan", columnDefinition = "TEXT")
    @Size(max = 5000, message = "Rencana perawatan maksimal 5000 karakter")
    private String rencanaPerawatan;

    @Column(name = "catatan_veterinarian", columnDefinition = "TEXT")
    @Size(max = 5000, message = "Catatan veterinarian maksimal 5000 karakter")
    private String catatanVeterinarian;

    @Column(name = "butuh_tindak_lanjut", nullable = false)
    private Boolean butuhTindakLanjut = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "janji_temu_id", nullable = false, unique = true)
    private Appointment janjiTemu;

    // Constructors
    public AppointmentResult() {
    }

    public AppointmentResult(String diagnosis, Appointment janjiTemu) {
        this.diagnosis = diagnosis;
        this.janjiTemu = janjiTemu;
    }

    public AppointmentResult(String diagnosis, String rencanaPerawatan, String catatanVeterinarian,
            Boolean butuhTindakLanjut, Appointment janjiTemu) {
        this.diagnosis = diagnosis;
        this.rencanaPerawatan = rencanaPerawatan;
        this.catatanVeterinarian = catatanVeterinarian;
        this.butuhTindakLanjut = butuhTindakLanjut;
        this.janjiTemu = janjiTemu;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getRencanaPerawatan() {
        return rencanaPerawatan;
    }

    public void setRencanaPerawatan(String rencanaPerawatan) {
        this.rencanaPerawatan = rencanaPerawatan;
    }

    public String getCatatanVeterinarian() {
        return catatanVeterinarian;
    }

    public void setCatatanVeterinarian(String catatanVeterinarian) {
        this.catatanVeterinarian = catatanVeterinarian;
    }

    public Boolean getButuhTindakLanjut() {
        return butuhTindakLanjut;
    }

    public void setButuhTindakLanjut(Boolean butuhTindakLanjut) {
        this.butuhTindakLanjut = butuhTindakLanjut;
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

    public Appointment getJanjiTemu() {
        return janjiTemu;
    }

    public void setJanjiTemu(Appointment janjiTemu) {
        this.janjiTemu = janjiTemu;
    }

    // Helper methods
    public boolean isFollowUpRequired() {
        return butuhTindakLanjut != null && butuhTindakLanjut;
    }

    public String getSummary() {
        return diagnosis != null && diagnosis.length() > 100
                ? diagnosis.substring(0, 100) + "..."
                : diagnosis;
    }

    @Override
    public String toString() {
        return "AppointmentResult{" +
                "id=" + id +
                ", diagnosis='" + getSummary() + '\'' +
                ", butuhTindakLanjut=" + butuhTindakLanjut +
                ", janjiTemu=" + (janjiTemu != null ? janjiTemu.getId() : "null") +
                '}';
    }
}
