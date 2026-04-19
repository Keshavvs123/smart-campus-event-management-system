package com.smcem.service;

import com.smcem.entity.Attendance;
import com.smcem.entity.AttendanceStatus;
import com.smcem.entity.Event;
import com.smcem.entity.Registration;
import com.smcem.entity.User;
import com.smcem.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    public Attendance markAttendance(Registration registration, AttendanceStatus status, User markedBy) {
        Optional<Attendance> existing = attendanceRepository.findByRegistration(registration);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Attendance already marked for this registration");
        }
        Attendance attendance = new Attendance();
        attendance.setRegistration(registration);
        attendance.setStatus(status);
        attendance.setMarkedBy(markedBy);
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> findByEvent(Event event) {
        return attendanceRepository.findByRegistration_Event(event);
    }

    public Optional<Attendance> findByRegistration(Registration registration) {
        return attendanceRepository.findByRegistration(registration);
    }

    public long countPresent(Event event) {
        return attendanceRepository.countByRegistration_EventAndStatus(event, AttendanceStatus.PRESENT);
    }

    public long countAbsent(Event event) {
        return attendanceRepository.countByRegistration_EventAndStatus(event, AttendanceStatus.ABSENT);
    }
}