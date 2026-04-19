package com.smcem.entity;

public enum CertificateStatus {
    EARNED("Earned - Attended Event"),
    COMPLIMENTARY("Complimentary - Did Not Attend"),
    NOT_ELIGIBLE("Not Eligible");

    private final String displayName;

    CertificateStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
