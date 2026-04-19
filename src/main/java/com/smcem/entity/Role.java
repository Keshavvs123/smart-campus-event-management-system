package com.smcem.entity;

public enum Role {
    STUDENT("Student"),
    ORGANIZER("Event Organizer"),
    FACULTY("Faculty"),
    ADMIN("Administrator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
