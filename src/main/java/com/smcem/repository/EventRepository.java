package com.smcem.repository;

import com.smcem.entity.Event;
import com.smcem.entity.EventStatus;
import com.smcem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByOrganizer(User organizer);

    List<Event> findByStatus(EventStatus status);

    @Query("SELECT e FROM Event e WHERE e.date < :currentDate AND e.status != 'COMPLETED'")
    List<Event> findEventsToComplete(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT e FROM Event e WHERE e.registrationDeadline < :currentDate AND e.status = 'OPEN'")
    List<Event> findEventsToClose(@Param("currentDate") LocalDate currentDate);
}