package com.smcem.repository;

import com.smcem.entity.Event;
import com.smcem.entity.Registration;
import com.smcem.entity.RegistrationStatus;
import com.smcem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    Optional<Registration> findByUserAndEvent(User user, Event event);

    List<Registration> findByEvent(Event event);

    List<Registration> findByUser(User user);

    List<Registration> findByEventAndStatus(Event event, RegistrationStatus status);

    long countByEventAndStatus(Event event, RegistrationStatus status);
}