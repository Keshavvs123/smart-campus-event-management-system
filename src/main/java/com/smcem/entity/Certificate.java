package com.smcem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id")
    private Registration registration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @Enumerated(EnumType.STRING)
    @NotNull
    private CertificateStatus certificateStatus;

    private String certificateNumber; // Unique certificate number
    private String filePath; // Path to PDF file

    @NotNull
    private LocalDateTime issuedDate = LocalDateTime.now();

    private LocalDateTime downloadedDate;
    private Integer downloadCount = 0;

    // Constructors
    public Certificate() {}

    public Certificate(Registration registration, User user, Event event, CertificateStatus certificateStatus) {
        this.registration = registration;
        this.user = user;
        this.event = event;
        this.certificateStatus = certificateStatus;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Registration getRegistration() { return registration; }
    public void setRegistration(Registration registration) { this.registration = registration; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public CertificateStatus getCertificateStatus() { return certificateStatus; }
    public void setCertificateStatus(CertificateStatus certificateStatus) { this.certificateStatus = certificateStatus; }

    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDateTime getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDateTime issuedDate) { this.issuedDate = issuedDate; }

    public LocalDateTime getDownloadedDate() { return downloadedDate; }
    public void setDownloadedDate(LocalDateTime downloadedDate) { this.downloadedDate = downloadedDate; }

    public Integer getDownloadCount() { return downloadCount; }
    public void setDownloadCount(Integer downloadCount) { this.downloadCount = downloadCount; }
}
