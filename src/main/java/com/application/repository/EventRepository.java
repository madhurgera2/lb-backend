package com.application.repository;

import com.application.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    // You can add custom queries here if needed, such as finding events by name or location
}
