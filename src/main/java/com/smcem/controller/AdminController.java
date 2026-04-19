package com.smcem.controller;

import com.smcem.entity.Event;
import com.smcem.entity.EventStatus;
import com.smcem.entity.Role;
import com.smcem.entity.User;
import com.smcem.service.EventService;
import com.smcem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private com.smcem.service.RegistrationService registrationService;

    @Autowired
    private com.smcem.service.AttendanceService attendanceService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        model.addAttribute("user", user);
        eventService.updateEventStatuses();
        List<Event> events = eventService.findAllEvents();
        List<User> users = userService.findAllUsers();
        long totalEvents = events.size();
        long totalStudents = users.stream().filter(u -> u.getRole() == Role.STUDENT).count();
        long openEvents = events.stream().filter(e -> e.getStatus() == EventStatus.OPEN).count();
        model.addAttribute("totalEvents", totalEvents);
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("openEvents", openEvents);
        model.addAttribute("events", events);
        return "admin/dashboard";
    }

    @PostMapping("/approve-event/{eventId}")
    public String approveEvent(@PathVariable Long eventId, RedirectAttributes redirectAttributes) {
        eventService.findById(eventId).ifPresent(e -> {
            e.setStatus(EventStatus.OPEN);
            eventService.saveEvent(e);
        });
        redirectAttributes.addFlashAttribute("success", "Event approved");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/reject-event/{eventId}")
    public String rejectEvent(@PathVariable Long eventId, RedirectAttributes redirectAttributes) {
        eventService.findById(eventId).ifPresent(e -> {
            e.setStatus(EventStatus.REJECTED);
            eventService.saveEvent(e);
        });
        redirectAttributes.addFlashAttribute("success", "Event rejected");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/payments")
    public String managePayments(Model model) {
        java.util.List<com.smcem.entity.Registration> payments = registrationService.findAll().stream()
            .filter(r -> r.getPaymentStatus() != com.smcem.entity.PaymentStatus.NOT_APPLICABLE)
            .collect(java.util.stream.Collectors.toList());
        model.addAttribute("payments", payments);
        return "admin/payments";
    }

    @PostMapping("/mark-paid/{registrationId}")
    public String markPaid(@PathVariable Long registrationId, RedirectAttributes redirectAttributes) {
        registrationService.findById(registrationId).ifPresent(r -> {
            r.setPaymentStatus(com.smcem.entity.PaymentStatus.PAID);
            registrationService.save(r);
        });
        redirectAttributes.addFlashAttribute("success", "Payment marked as paid!");
        return "redirect:/admin/payments";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        java.util.List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/users/{userId}/change-role")
    public String changeUserRole(@PathVariable Long userId, @org.springframework.web.bind.annotation.RequestParam String role, RedirectAttributes redirectAttributes) {
        userService.findById(userId).ifPresent(u -> {
            u.setRole(com.smcem.entity.Role.valueOf(role));
            userService.saveUser(u);
        });
        redirectAttributes.addFlashAttribute("success", "User role updated successfully");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{userId}/delete")
    public String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        userService.deleteUser(userId);
        redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        return "redirect:/admin/users";
    }

    @GetMapping("/reports")
    public String viewReports(Model model) {
        java.util.List<Event> events = eventService.findAllEvents();
        java.util.List<java.util.Map<String, Object>> eventStats = new java.util.ArrayList<>();
        double totalRevenue = 0;
        
        for (Event e : events) {
            java.util.Map<String, Object> stat = new java.util.HashMap<>();
            stat.put("event", e);
            long totalRegs = registrationService.countApproved(e);
            long presentCount = attendanceService.countPresent(e);
            
            // Calculate revenue
            double revenue = 0;
            if (e.getFee() != null && e.getFee() > 0) {
                long paidCount = registrationService.findByEvent(e).stream()
                    .filter(r -> r.getPaymentStatus() == com.smcem.entity.PaymentStatus.PAID)
                    .count();
                revenue = paidCount * e.getFee();
            }
            totalRevenue += revenue;
            
            stat.put("totalRegs", totalRegs);
            stat.put("presentCount", presentCount);
            stat.put("revenue", revenue);
            
            double turnout = totalRegs > 0 ? ((double) presentCount / totalRegs) * 100 : 0;
            stat.put("turnout", Math.round(turnout * 100.0) / 100.0);
            
            eventStats.add(stat);
        }
        model.addAttribute("eventStats", eventStats);
        model.addAttribute("totalRevenue", totalRevenue);
        return "admin/reports";
    }

    private User getCurrentUser(Authentication authentication) {
        return userService.findByUsername(authentication.getName()).orElseThrow();
    }
}