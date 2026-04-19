package com.smcem.repository;

import com.smcem.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByRegistration(Registration registration);
    List<Certificate> findByUser(User user);
    List<Certificate> findByEvent(Event event);
    List<Certificate> findByCertificateStatus(CertificateStatus status);
    
    @Query("SELECT c FROM Certificate c WHERE c.certificateNumber = :certificateNumber")
    Optional<Certificate> findByCertificateNumber(@Param("certificateNumber") String certificateNumber);
    
    @Query("SELECT c FROM Certificate c WHERE c.user = :user AND c.event = :event")
    Optional<Certificate> findByUserAndEvent(@Param("user") User user, @Param("event") Event event);
    
    @Query("SELECT COUNT(c) FROM Certificate c WHERE c.event = :event AND c.certificateStatus = 'EARNED'")
    Long countEarnedByEvent(@Param("event") Event event);
}
