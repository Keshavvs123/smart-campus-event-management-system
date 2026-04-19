package com.smcem.controller;

import com.smcem.entity.Attendance;
import com.smcem.entity.AttendanceStatus;
import com.smcem.entity.Event;
import com.smcem.entity.EventStatus;
import com.smcem.entity.Registration;
import com.smcem.entity.RegistrationStatus;
import com.smcem.entity.User;
import com.smcem.service.AttendanceService;
import com.smcem.service.EventService;
import com.smcem.service.RegistrationService;
import com.smcem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        model.addAttribute("user", user);
        eventService.updateEventStatuses();
        List<Event> events = eventService.findByStatus(EventStatus.OPEN);
        List<Event> completedEvents = eventService.findByStatus(EventStatus.COMPLETED);
        model.addAttribute("events", events);
        model.addAttribute("completedEvents", completedEvents);

        Map<Long, Long> seatsLeftMap = events.stream()
                .collect(Collectors.toMap(Event::getId, event -> event.getCapacity() - registrationService.countApproved(event)));
        Set<Long> registeredEvents = registrationService.findByUser(user).stream()
                .map(registration -> registration.getEvent().getId())
                .collect(Collectors.toSet());
        model.addAttribute("seatsLeftMap", seatsLeftMap);
        model.addAttribute("registeredEvents", registeredEvents);
        return "student/dashboard";
    }

    @PostMapping("/register/{eventId}")
    public String registerForEvent(@PathVariable Long eventId,
                                   @RequestParam(required = false) Integer age,
                                   @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate dateOfBirth,
                                   @RequestParam(required = false) Double cgpa,
                                   Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(authentication);
        if (age != null) user.setAge(age);
        if (dateOfBirth != null) user.setDateOfBirth(dateOfBirth);
        if (cgpa != null) user.setCgpa(cgpa);
        userService.saveUser(user);

        Event event = eventService.findById(eventId).orElseThrow();
        try {
            Registration reg = registrationService.registerForEvent(user, event);
            if (reg.getPaymentStatus() == com.smcem.entity.PaymentStatus.PENDING) {
                redirectAttributes.addFlashAttribute("info", "Please complete your payment to finalize registration.");
                return "redirect:/student/payment/" + reg.getId();
            }
            redirectAttributes.addFlashAttribute("success", "Registered successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "System Error: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/student/dashboard";
    }

    @GetMapping("/my-registrations")
    public String myRegistrations(Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        List<Registration> registrations = registrationService.findByUser(user);
        model.addAttribute("registrations", registrations);
        return "student/my-registrations";
    }

    @GetMapping("/payment/{regId}")
    public String paymentPage(@PathVariable Long regId, Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(authentication);
        Optional<Registration> regOpt = registrationService.findById(regId);
        
        if (regOpt.isEmpty() || !regOpt.get().getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "Invalid registration form");
            return "redirect:/student/dashboard";
        }
        
        Registration reg = regOpt.get();
        if (reg.getPaymentStatus() == com.smcem.entity.PaymentStatus.PAID) {
            redirectAttributes.addFlashAttribute("success", "Payment already completed!");
            return "redirect:/student/my-registrations";
        }
        
        model.addAttribute("registration", reg);
        model.addAttribute("event", reg.getEvent());
        model.addAttribute("user", user);
        return "student/payment";
    }

    @PostMapping("/process-payment/{regId}")
    public String processPayment(@PathVariable Long regId, @RequestParam String cardNumber, Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(authentication);
        Optional<Registration> regOpt = registrationService.findById(regId);
        
        if (regOpt.isPresent() && regOpt.get().getUser().getId().equals(user.getId())) {
            Registration reg = regOpt.get();
            // Simulate payment processing...
            reg.setPaymentStatus(com.smcem.entity.PaymentStatus.PAID);
            registrationService.save(reg);
            redirectAttributes.addFlashAttribute("success", "Payment of $" + reg.getEvent().getFee() + " processed successfully! Registration complete.");
        }
        return "redirect:/student/my-registrations";
    }

    @GetMapping("/certificate/{eventId}")
    public String downloadCertificate(@PathVariable Long eventId, Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(authentication);
        Event event = eventService.findById(eventId).orElseThrow();
        Optional<Registration> registration = registrationService.findByUserAndEvent(user, event);
        if (registration.isPresent() && registration.get().getStatus() == RegistrationStatus.APPROVED) {
            Optional<Attendance> attendance = attendanceService.findByRegistration(registration.get());
            if (attendance.isPresent() && attendance.get().getStatus() == AttendanceStatus.PRESENT) {
                if (registration.get().getPaymentStatus() == com.smcem.entity.PaymentStatus.PENDING) {
                    redirectAttributes.addFlashAttribute("error", "Pay the fee then only you can download the certificate.");
                    return "redirect:/student/dashboard";
                }
                model.addAttribute("event", event);
                model.addAttribute("user", user);
                return "student/certificate";
            } else {
                redirectAttributes.addFlashAttribute("error", "You didn't attend the event and your certificate cannot be generated.");
                return "redirect:/student/dashboard";
            }
        }
        redirectAttributes.addFlashAttribute("error", "You are not eligible for a certificate for this event.");
        return "redirect:/student/dashboard";
    }

    private User getCurrentUser(Authentication authentication) {
        return userService.findByUsername(authentication.getName()).orElseThrow();
    }
}