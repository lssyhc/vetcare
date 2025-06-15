package com.lima.vetcare.model;

public enum AppointmentStatus {
    SCHEDULED("Terjadwal"),
    COMPLETED("Selesai"),
    CANCELLED("Dibatalkan");

    private final String displayName;

    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
