package com.smcem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    @Size(max = 1000)
    private String description;

    @NotNull
    @Future
    private LocalDate date;

    @NotNull
    private LocalTime time;

    @NotBlank
    @Size(max = 200)
    private String venue;

    @NotNull
    @Min(1)
    private Integer capacity;

    @NotNull
    private LocalDate registrationDeadline;

    @NotNull
    private LocalTime registrationDeadlineTime = LocalTime.of(23, 59);

    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private Double cgpaCriteria;

    @DecimalMin("0.0")
    private Double fee = 0.0;

    @Min(18)
    @Max(50)
    private Integer ageMin = 18;

    @Min(18)
    @Max(50)
    private Integer ageMax = 50;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EventStatus status = EventStatus.DRAFT;

    private String imagePath; // Path to event image
    private String imageFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id")
    private User organizer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_reviewer_id")
    private User facultyReviewer;

    private String facultyReviewComments;
    private LocalDateTime reviewedDate;

    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime updatedDate = LocalDateTime.now();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Registration> registrations;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Attendance> attendances;

    // Constructors
    public Event() {}

    public Event(String title, String description, LocalDate date, LocalTime time, String venue, Integer capacity, LocalDate registrationDeadline, User organizer) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.capacity = capacity;
        this.registrationDeadline = registrationDeadline;
        this.organizer = organizer;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public LocalDate getRegistrationDeadline() { return registrationDeadline; }
    public void setRegistrationDeadline(LocalDate registrationDeadline) { this.registrationDeadline = registrationDeadline; }

    public LocalTime getRegistrationDeadlineTime() { return registrationDeadlineTime; }
    public void setRegistrationDeadlineTime(LocalTime registrationDeadlineTime) { this.registrationDeadlineTime = registrationDeadlineTime; }

    public Double getCgpaCriteria() { return cgpaCriteria; }
    public void setCgpaCriteria(Double cgpaCriteria) { this.cgpaCriteria = cgpaCriteria; }

    public Integer getAgeMin() { return ageMin; }
    public void setAgeMin(Integer ageMin) { this.ageMin = ageMin; }

    public Integer getAgeMax() { return ageMax; }
    public void setAgeMax(Integer ageMax) { this.ageMax = ageMax; }

    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getImageFileName() { return imageFileName; }
    public void setImageFileName(String imageFileName) { this.imageFileName = imageFileName; }

    public User getOrganizer() { return organizer; }
    public void setOrganizer(User organizer) { this.organizer = organizer; }

    public User getFacultyReviewer() { return facultyReviewer; }
    public void setFacultyReviewer(User facultyReviewer) { this.facultyReviewer = facultyReviewer; }

    public String getFacultyReviewComments() { return facultyReviewComments; }
    public void setFacultyReviewComments(String facultyReviewComments) { this.facultyReviewComments = facultyReviewComments; }

    public LocalDateTime getReviewedDate() { return reviewedDate; }
    public void setReviewedDate(LocalDateTime reviewedDate) { this.reviewedDate = reviewedDate; }

    public Double getFee() { return fee; }
    public void setFee(Double fee) { this.fee = fee; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }

    public Set<Registration> getRegistrations() { return registrations; }
    public void setRegistrations(Set<Registration> registrations) { this.registrations = registrations; }

    public Set<Attendance> getAttendances() { return attendances; }
    public void setAttendances(Set<Attendance> attendances) { this.attendances = attendances; }
}