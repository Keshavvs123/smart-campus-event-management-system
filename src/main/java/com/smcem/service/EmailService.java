package com.smcem.service;

import com.smcem.entity.*;
import com.smcem.repository.PasswordResetRepository;
import com.smcem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.url}")
    private String appUrl;

    @Value("${app.mail.from}")
    private String mailFrom;

    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }

    public String sendPasswordResetEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("Email not found in system");
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);

        PasswordReset reset = new PasswordReset();
        reset.setUser(user);
        reset.setToken(token);
        reset.setExpiryDate(expiryDate);
        passwordResetRepository.save(reset);

        String resetLink = appUrl + "/reset-password?token=" + token;
        String subject = "Password Reset Request - SMCEM";
        String body = "Dear " + user.getName() + ",\n\n" +
                "You have requested to reset your password. Please click the link below to reset:\n\n" +
                resetLink + "\n\n" +
                "This link will expire in 24 hours.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Best regards,\nSMCEM Administration";

        sendSimpleEmail(email, subject, body);
        return token;
    }

    public void sendEventApprovalNotification(User organizer, Event event, boolean approved) {
        String status = approved ? "APPROVED" : "REJECTED";
        String subject = "Event " + status + " - " + event.getTitle();
        String body = "Dear " + organizer.getName() + ",\n\n" +
                "Your event \"" + event.getTitle() + "\" has been " + status + " by Faculty.\n\n" +
                "Event Details:\n" +
                "Date: " + event.getDate() + "\n" +
                "Time: " + event.getTime() + "\n" +
                "Venue: " + event.getVenue() + "\n\n";
        
        if (!approved && event.getFacultyReviewComments() != null) {
            body += "Comments: " + event.getFacultyReviewComments() + "\n\n";
        }

        body += "Best regards,\nSMCEM Administration";
        sendSimpleEmail(organizer.getEmail(), subject, body);
    }

    public void sendCertificateNotification(User student, Event event, boolean isEarned) {
        String type = isEarned ? "Certificate of Participation" : "Complimentary Certificate";
        String subject = "Your " + type + " for " + event.getTitle();
        String body = "Dear " + student.getName() + ",\n\n" +
                "Congratulations! Your " + type + " for the event \"" + event.getTitle() + "\" is now available.\n\n" +
                "Event: " + event.getTitle() + "\n" +
                "Date: " + event.getDate() + "\n\n" +
                "You can download your certificate from your dashboard.\n\n" +
                "Best regards,\nSMCEM Administration";

        sendSimpleEmail(student.getEmail(), subject, body);
    }

    public void sendRegistrationConfirmation(User student, Event event) {
        String subject = "Registration Confirmation - " + event.getTitle();
        String body = "Dear " + student.getName() + ",\n\n" +
                "Thank you for registering for the event \"" + event.getTitle() + "\".\n\n" +
                "Event Details:\n" +
                "Date: " + event.getDate() + "\n" +
                "Time: " + event.getTime() + "\n" +
                "Venue: " + event.getVenue() + "\n\n" +
                "Please arrive 15 minutes before the event starts.\n\n" +
                "Best regards,\nSMCEM Administration";

        sendSimpleEmail(student.getEmail(), subject, body);
    }

    public void sendEventCancelledNotification(Event event) {
        String subject = "Event Cancelled - " + event.getTitle();
        String body = "We regret to inform you that the event \"" + event.getTitle() + "\" scheduled for " + event.getDate() + " has been cancelled.\n\n" +
                "We apologize for any inconvenience.\n\n" +
                "Best regards,\nSMCEM Administration";

        for (Registration reg : event.getRegistrations()) {
            sendSimpleEmail(reg.getUser().getEmail(), subject, body);
        }
    }
}
