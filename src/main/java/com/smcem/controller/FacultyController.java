package com.smcem.controller;

import com.smcem.entity.Event;
import com.smcem.entity.EventStatus;
import com.smcem.entity.Role;
import com.smcem.entity.User;
import com.smcem.service.EmailService;
import com.smcem.service.EventService;
import com.smcem.service.NotificationService;
import com.smcem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/faculty")
public class FacultyController {

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        model.addAttribute("user", user);
        List<User> organizers = userService.findAllUsers().stream()
            .filter(u -> u.getRole() == Role.ORGANIZER)
            .toList();
        model.addAttribute("organizers", organizers);

        List<Event> pendingEvents = eventService.findByStatus(EventStatus.PENDING_APPROVAL);
        model.addAttribute("pendingEvents", pendingEvents);
        
        List<Event> allEvents = eventService.findAllEvents();
        List<Event> activeEvents = allEvents.stream()
            .filter(e -> e.getStatus() == EventStatus.OPEN)
            .toList();
        List<Event> completedEvents = allEvents.stream()
            .filter(e -> e.getStatus() == EventStatus.COMPLETED)
            .toList();
            
        model.addAttribute("activeEvents", activeEvents);
        model.addAttribute("completedEvents", completedEvents);
        
        return "faculty/dashboard";
    }

    @PostMapping("/send-notification")
    public String sendNotification(@RequestParam Long receiverId, @RequestParam String message, Authentication authentication, RedirectAttributes redirectAttributes) {
        User sender = getCurrentUser(authentication);
        User receiver = userService.findById(receiverId).orElseThrow();
        notificationService.sendNotification(sender, receiver, message);
        redirectAttributes.addFlashAttribute("success", "Notification sent");
        return "redirect:/faculty/dashboard";
    }

    @PostMapping("/approve-event/{eventId}")
    public String approveEvent(@PathVariable Long eventId, Authentication authentication, RedirectAttributes redirectAttributes) {
        User faculty = getCurrentUser(authentication);
        Event event = eventService.findById(eventId).orElseThrow();
        event.setFacultyReviewer(faculty);
        event.setReviewedDate(LocalDateTime.now());
        event.setStatus(EventStatus.OPEN);
        eventService.saveEvent(event);
        notificationService.sendNotification(faculty, event.getOrganizer(), "Your event '" + event.getTitle() + "' has been approved.");
        emailService.sendEventApprovalNotification(event.getOrganizer(), event, true);
        redirectAttributes.addFlashAttribute("success", "Event approved successfully");
        return "redirect:/faculty/dashboard";
    }

    @PostMapping("/reject-event/{eventId}")
    public String rejectEvent(@PathVariable Long eventId, @RequestParam(required = false) String comments, Authentication authentication, RedirectAttributes redirectAttributes) {
        User faculty = getCurrentUser(authentication);
        Event event = eventService.findById(eventId).orElseThrow();
        event.setFacultyReviewer(faculty);
        event.setReviewedDate(LocalDateTime.now());
        event.setFacultyReviewComments(comments);
        event.setStatus(EventStatus.REJECTED);
        eventService.saveEvent(event);
        notificationService.sendNotification(faculty, event.getOrganizer(), "Your event '" + event.getTitle() + "' has been rejected. Comments: " + (comments != null ? comments : "None"));
        emailService.sendEventApprovalNotification(event.getOrganizer(), event, false);
        redirectAttributes.addFlashAttribute("success", "Event rejected successfully");
        return "redirect:/faculty/dashboard";
    }

    private User getCurrentUser(Authentication authentication) {
        return userService.findByUsername(authentication.getName()).orElseThrow();
    }
}