package com.lima.vetcare.model;

public enum AppointmentDuration {
    THIRTY_MINUTES(30, "30 minutes"),
    SIXTY_MINUTES(60, "60 minutes"),
    NINETY_MINUTES(90, "90 minutes");

    private final int minutes;
    private final String displayName;

    AppointmentDuration(int minutes, String displayName) {
        this.minutes = minutes;
        this.displayName = displayName;
    }

    public int getMinutes() {
        return minutes;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static AppointmentDuration fromMinutes(int minutes) {
        for (AppointmentDuration duration : values()) {
            if (duration.getMinutes() == minutes) {
                return duration;
            }
        }
        throw new IllegalArgumentException("Invalid duration: " + minutes + " minutes");
    }
}
