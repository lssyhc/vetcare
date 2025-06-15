package com.lima.vetcare.model;

public enum AppointmentStatus {
    SCHEDULED("TERJADWAL"),
    COMPLETED("SELESAI"),
    CANCELLED("DIBATALKAN");

    private final String displayName;

    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
