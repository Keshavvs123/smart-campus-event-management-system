package com.smcem.service;

import com.smcem.entity.*;
import com.smcem.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Value("${file.certificate-dir}")
    private String certificateDir;

    @Value("${app.certificate.issuer}")
    private String certificateIssuer;

    public Certificate generateCertificate(Registration registration, boolean attended) {
        User student = registration.getUser();
        Event event = registration.getEvent();

        CertificateStatus status = attended ? CertificateStatus.EARNED : CertificateStatus.COMPLIMENTARY;
        
        Optional<Certificate> existing = certificateRepository.findByUserAndEvent(student, event);
        Certificate certificate;
        
        if (existing.isPresent()) {
            certificate = existing.get();
            certificate.setCertificateStatus(status);
        } else {
            certificate = new Certificate();
            certificate.setRegistration(registration);
            certificate.setUser(student);
            certificate.setEvent(event);
            certificate.setCertificateStatus(status);
            certificate.setCertificateNumber(generateCertificateNumber(student.getId(), event.getId()));
            certificate.setIssuedDate(LocalDateTime.now());
        }

        String pdfPath = generatePDF(certificate);
        certificate.setFilePath(pdfPath);
        
        return certificateRepository.save(certificate);
    }

    public String generatePDF(Certificate certificate) {
        try {
            String certificateFileName = "cert_" + certificate.getUser().getId() + "_" + 
                                        certificate.getEvent().getId() + ".pdf";
            String outputPath = certificateDir + File.separator + certificateFileName;

            String titleText = "Certificate of " + (certificate.getCertificateStatus() == CertificateStatus.EARNED ? "Participation" : "Participation");
            String achievement = (certificate.getCertificateStatus() == CertificateStatus.EARNED ?
                    "has successfully attended and completed" : "has participated in") + " the event";
            String dateFormatted = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));

            // USING THE CREATIONAL BUILDER PATTERN
            CertificateBuilder builder = new CertificateBuilder();
            return builder.setOutputPath(outputPath)
                          .setTitle(titleText)
                          .setIssuer(certificateIssuer)
                          .setRecipientName(certificate.getUser().getName())
                          .setAchievementText(achievement)
                          .setEventName(certificate.getEvent().getTitle())
                          .setDate(dateFormatted)
                          .setCertificateNumber(certificate.getCertificateNumber())
                          .build();

        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF certificate: " + e.getMessage(), e);
        }
    }

    private String generateCertificateNumber(Long userId, Long eventId) {
        return String.format("CERT-%d-%d-%d", userId, eventId, System.currentTimeMillis() % 10000);
    }

    public Optional<Certificate> getCertificateByNumber(String certificateNumber) {
        return certificateRepository.findByCertificateNumber(certificateNumber);
    }

    public List<Certificate> getUserCertificates(User user) {
        return certificateRepository.findByUser(user);
    }

    public List<Certificate> getEventCertificates(Event event) {
        return certificateRepository.findByEvent(event);
    }

    public Optional<Certificate> getUserEventCertificate(User user, Event event) {
        return certificateRepository.findByUserAndEvent(user, event);
    }

    public void downloadCertificate(Certificate certificate) {
        certificate.setDownloadedDate(LocalDateTime.now());
        certificate.setDownloadCount((certificate.getDownloadCount() != null ? certificate.getDownloadCount() : 0) + 1);
        certificateRepository.save(certificate);
    }

    public void deleteCertificate(Long certificateId) {
        if (certificateId == null) {
            throw new IllegalArgumentException("Certificate ID cannot be null");
        }
        Optional<Certificate> cert = certificateRepository.findById(certificateId);
        if (cert.isPresent() && cert.get().getFilePath() != null) {
            File file = new File(cert.get().getFilePath());
            if (file.exists()) {
                file.delete();
            }
        }
        certificateRepository.deleteById(certificateId);
    }
}
