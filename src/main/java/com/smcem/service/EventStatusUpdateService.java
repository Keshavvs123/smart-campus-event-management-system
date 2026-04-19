package com.smcem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class EventStatusUpdateService {

    @Autowired
    private EventService eventService;

    @Scheduled(cron = "0 0 0 * * ?") // Run daily at midnight
    public void updateEventStatuses() {
        eventService.updateEventStatuses();
    }
}