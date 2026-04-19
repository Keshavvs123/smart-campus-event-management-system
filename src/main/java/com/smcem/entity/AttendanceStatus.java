package com.smcem.entity;

public enum AttendanceStatus {
    PRESENT("Present"),
    ABSENT("Absent"),
    EXCUSED("Excused");

    private final String displayName;

    AttendanceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
