package com.smcem.controller;

import com.smcem.entity.Attendance;
import com.smcem.entity.AttendanceStatus;
import com.smcem.entity.Event;
import com.smcem.entity.EventStatus;
import com.smcem.entity.Notification;
import com.smcem.entity.Registration;
import com.smcem.entity.User;
import com.smcem.service.AttendanceService;
import com.smcem.service.EventService;
import com.smcem.service.NotificationService;
import com.smcem.service.RegistrationService;
import com.smcem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/organizer")
public class OrganizerController {

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        model.addAttribute("user", user);
        eventService.updateEventStatuses();
        List<Event> events = eventService.findByOrganizer(user);
        
        List<Event> activeEvents = events.stream()
                .filter(e -> e.getStatus() != EventStatus.COMPLETED && e.getStatus() != EventStatus.DELETED && e.getStatus() != EventStatus.CANCELLED)
                .collect(java.util.stream.Collectors.toList());
        List<Event> completedEvents = events.stream()
                .filter(e -> e.getStatus() == EventStatus.COMPLETED)
                .collect(java.util.stream.Collectors.toList());
        List<Event> deletedEvents = events.stream()
                .filter(e -> e.getStatus() == EventStatus.DELETED || e.getStatus() == EventStatus.CANCELLED)
                .collect(java.util.stream.Collectors.toList());

        List<Notification> notifications = notificationService.findByReceiver(user);
        long unreadCount = notificationService.countUnreadByReceiver(user);
        
        model.addAttribute("activeEvents", activeEvents);
        model.addAttribute("completedEvents", completedEvents);
        model.addAttribute("deletedEvents", deletedEvents);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        return "organizer/dashboard";
    }

    @PostMapping("/mark-notification-read/{id}")
    public String markNotificationRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/organizer/dashboard";
    }

    @GetMapping("/create-event")
    public String createEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "organizer/create-event";
    }

    @PostMapping("/create-event")
    public String createEvent(@Valid @ModelAttribute("event") Event event, BindingResult result, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "organizer/create-event";
        }
        User organizer = getCurrentUser(authentication);
        event.setOrganizer(organizer);
        event.setStatus(EventStatus.PENDING_APPROVAL);
        try {
            eventService.saveEvent(event);
            redirectAttributes.addFlashAttribute("success", "Event submitted for faculty approval");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "organizer/create-event";
        }
        return "redirect:/organizer/dashboard";
    }

    @GetMapping("/event/{eventId}/edit")
    public String editEventForm(@PathVariable Long eventId, Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        User organizer = getCurrentUser(authentication);
        Event event = eventService.findById(eventId).orElseThrow();
        if (!event.getOrganizer().getId().equals(organizer.getId())) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to edit this event.");
            return "redirect:/organizer/dashboard";
        }
        model.addAttribute("event", event);
        return "organizer/edit-event";
    }

    @PostMapping("/event/{eventId}/edit")
    public String editEvent(@PathVariable Long eventId, @Valid @ModelAttribute("event") Event event, BindingResult result, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "organizer/edit-event";
        }
        User organizer = getCurrentUser(authentication);
        Event existingEvent = eventService.findById(eventId).orElseThrow();
        if (!existingEvent.getOrganizer().getId().equals(organizer.getId())) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to edit this event.");
            return "redirect:/organizer/dashboard";
        }
        existingEvent.setTitle(event.getTitle());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setDate(event.getDate());
        existingEvent.setTime(event.getTime());
        existingEvent.setVenue(event.getVenue());
        existingEvent.setCapacity(event.getCapacity());
        existingEvent.setRegistrationDeadline(event.getRegistrationDeadline());
        existingEvent.setRegistrationDeadlineTime(event.getRegistrationDeadlineTime());
        existingEvent.setCgpaCriteria(event.getCgpaCriteria());
        existingEvent.setFee(event.getFee());
        existingEvent.setAgeMin(event.getAgeMin());
        existingEvent.setAgeMax(event.getAgeMax());
        existingEvent.setStatus(EventStatus.PENDING_APPROVAL);
        existingEvent.setFacultyReviewComments(null);
        existingEvent.setFacultyReviewer(null);
        existingEvent.setReviewedDate(null);
        try {
            eventService.saveEvent(existingEvent);
            redirectAttributes.addFlashAttribute("success", "Event updated and resubmitted for faculty approval");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "organizer/edit-event";
        }
        return "redirect:/organizer/dashboard";
    }

    @PostMapping("/event/{eventId}/delete")
    public String deleteEvent(@PathVariable Long eventId, Authentication authentication, RedirectAttributes redirectAttributes) {
        User organizer = getCurrentUser(authentication);
        Event existingEvent = eventService.findById(eventId).orElseThrow();
        if (!existingEvent.getOrganizer().getId().equals(organizer.getId())) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to delete this event.");
            return "redirect:/organizer/dashboard";
        }
        existingEvent.setStatus(EventStatus.DELETED);
        eventService.saveEvent(existingEvent);
        redirectAttributes.addFlashAttribute("success", "Event deleted successfully");
        return "redirect:/organizer/dashboard";
    }

    @GetMapping("/event/{eventId}/registrations")
    public String viewRegistrations(@PathVariable Long eventId, Model model) {
        Event event = eventService.findById(eventId).orElseThrow();
        List<Registration> registrations = registrationService.findByEvent(event);
        model.addAttribute("event", event);
        model.addAttribute("registrations", registrations);
        return "organizer/registrations";
    }

    @PostMapping("/event/{eventId}/attendance")
    public String markBulkAttendance(@PathVariable Long eventId, @RequestParam Map<String, String> allParams, Authentication authentication, RedirectAttributes redirectAttributes) {
        User markedBy = getCurrentUser(authentication);
        eventService.findById(eventId).orElseThrow();
        int count = 0;
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("status_")) {
                try {
                    Long regId = Long.parseLong(entry.getKey().substring("status_".length()));
                    AttendanceStatus status = AttendanceStatus.valueOf(entry.getValue());
                    Registration registration = registrationService.findById(regId).orElseThrow();
                    if (registration.getEvent().getId().equals(eventId) && registration.getStatus() == com.smcem.entity.RegistrationStatus.APPROVED) {
                        attendanceService.markAttendance(registration, status, markedBy);
                        count++;
                    }
                } catch (Exception e) {
                    // Ignore parse errors or invalid statuses for safety across bulk items
                }
            }
        }
        redirectAttributes.addFlashAttribute("success", "Saved attendance for " + count + " students.");
        return "redirect:/organizer/event/" + eventId + "/attendance";
    }

    @GetMapping("/event/{eventId}/attendance")
    public String viewAttendance(@PathVariable Long eventId, Model model) {
        Event event = eventService.findById(eventId).orElseThrow();
        List<Registration> approvedRegs = registrationService.findByEvent(event).stream()
                .filter(r -> r.getStatus() == com.smcem.entity.RegistrationStatus.APPROVED)
                .collect(java.util.stream.Collectors.toList());
        
        List<Attendance> attendances = attendanceService.findByEvent(event);
        Map<Long, String> currentStatuses = attendances.stream()
                .collect(java.util.stream.Collectors.toMap(a -> a.getRegistration().getId(), a -> a.getStatus().name()));
        
        long present = attendanceService.countPresent(event);
        long absent = attendanceService.countAbsent(event);
        
        model.addAttribute("event", event);
        model.addAttribute("approvedRegs", approvedRegs);
        model.addAttribute("currentStatuses", currentStatuses);
        model.addAttribute("present", present);
        model.addAttribute("absent", absent);
        return "organizer/attendance";
    }

    private User getCurrentUser(Authentication authentication) {
        return userService.findByUsername(authentication.getName()).orElseThrow();
    }
}