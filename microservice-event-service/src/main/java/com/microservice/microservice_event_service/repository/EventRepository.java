package com.microservice.microservice_event_service.repository;

import com.microservice.microservice_event_service.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {}
