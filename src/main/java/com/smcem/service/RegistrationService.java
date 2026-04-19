package com.smcem.service;

import com.smcem.entity.Event;
import com.smcem.entity.EventStatus;
import com.smcem.entity.Registration;
import com.smcem.entity.RegistrationStatus;
import com.smcem.entity.User;
import com.smcem.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    public Registration registerForEvent(User user, Event event) {
        if (registrationRepository.findByUserAndEvent(user, event).isPresent()) {
            throw new IllegalArgumentException("Already registered for this event");
        }
        if (event.getStatus() != EventStatus.OPEN) {
            throw new IllegalArgumentException("Event is not open for registration");
        }
        long approvedCount = registrationRepository.countByEventAndStatus(event, RegistrationStatus.APPROVED);
        if (approvedCount >= event.getCapacity()) {
            throw new IllegalArgumentException("Event is full");
        }

        Registration registration = new Registration(user, event);
        registration.setStatus(evaluateRegistration(user, event));
        if (event.getFee() != null && event.getFee() > 0) {
            registration.setPaymentStatus(com.smcem.entity.PaymentStatus.PENDING);
        } else {
            registration.setPaymentStatus(com.smcem.entity.PaymentStatus.NOT_APPLICABLE);
        }
        return registrationRepository.save(registration);
    }

    public List<Registration> findByEvent(Event event) {
        return registrationRepository.findByEvent(event);
    }

    public List<Registration> findByUser(User user) {
        return registrationRepository.findByUser(user);
    }

    public List<Registration> findAll() {
        return registrationRepository.findAll();
    }

    public Optional<Registration> findByUserAndEvent(User user, Event event) {
        return registrationRepository.findByUserAndEvent(user, event);
    }

    public long countApproved(Event event) {
        return registrationRepository.countByEventAndStatus(event, RegistrationStatus.APPROVED);
    }

    public Registration save(Registration registration) {
        if (registration == null) {
            throw new IllegalArgumentException("Registration cannot be null");
        }
        return registrationRepository.save(registration);
    }

    public Optional<Registration> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return registrationRepository.findById(id);
    }

    private RegistrationStatus evaluateRegistration(User user, Event event) {
        if (user.getCgpa() != null && event.getCgpaCriteria() != null && user.getCgpa() < event.getCgpaCriteria()) {
            return RegistrationStatus.REJECTED;
        }
        if (user.getAge() != null) {
            if (event.getAgeMin() != null && user.getAge() < event.getAgeMin()) {
                return RegistrationStatus.REJECTED;
            }
            if (event.getAgeMax() != null && user.getAge() > event.getAgeMax()) {
                return RegistrationStatus.REJECTED;
            }
        }
        return RegistrationStatus.APPROVED;
    }
}