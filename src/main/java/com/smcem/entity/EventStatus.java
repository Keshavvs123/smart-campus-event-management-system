package com.smcem.entity;

public enum EventStatus {
    DRAFT("Draft"),
    PENDING_APPROVAL("Pending Faculty Approval"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    OPEN("Open for Registration"),
    CLOSED("Registration Closed"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    DELETED("Deleted");

    private final String displayName;

    EventStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
