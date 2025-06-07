package com.lima.vetcare.model;

/**
 * Enum untuk status janji temu dalam sistem VetCare
 */
public enum AppointmentStatus {
    TERJADWAL("Terjadwal"),
    BATAL("Dibatalkan"),
    SELESAI("Selesai");

    private final String displayName;

    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
