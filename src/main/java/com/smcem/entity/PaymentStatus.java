package com.smcem.entity;

public enum PaymentStatus {
    PENDING("Pending"),
    PAID("Paid"),
    NOT_APPLICABLE("Not Applicable");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
