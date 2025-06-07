package com.lima.vetcare.model;

/**
 * Enum untuk role pengguna dalam sistem VetCare
 */
public enum Role {
    PEMILIK("Pemilik Hewan"),
    VETERINARIAN("Veterinarian"),
    ADMIN("Administrator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
