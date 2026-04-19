package com.smcem.repository;

import com.smcem.entity.Attendance;
import com.smcem.entity.AttendanceStatus;
import com.smcem.entity.Event;
import com.smcem.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByRegistration(Registration registration);

    List<Attendance> findByRegistration_Event(Event event);

    long countByRegistration_EventAndStatus(Event event, AttendanceStatus status);
}