package com.uber_lite.uber_lite.core.matching.pricing.ride.state.events;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uber_lite.uber_lite.core.matching.pricing.ride.state.events.models.DriverAssignedEvent;
import com.uber_lite.uber_lite.core.matching.pricing.ride.state.events.models.DriverFreedEvent;
import com.uber_lite.uber_lite.core.matching.pricing.ride.state.events.models.RideCancelledEvent;
import com.uber_lite.uber_lite.core.matching.pricing.ride.state.events.models.RideCompletedEvent;
import com.uber_lite.uber_lite.core.matching.pricing.ride.state.events.models.RideStartedEvent;
import com.uber_lite.uber_lite.domain.RideAuditEvent;
import com.uber_lite.uber_lite.repo.RideAuditEventRepository;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Component
@RequiredArgsConstructor
public class AuditLogger {

    private EventBus eventBus;
    private RideAuditEventRepository auditRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void subscribeAll() {
        eventBus.subscribe(DriverAssignedEvent.class, this::onDriverAssigned);
        eventBus.subscribe(RideStartedEvent.class, this::onRideStarted);
        eventBus.subscribe(RideCompletedEvent.class, this::onRideCompleted);
        eventBus.subscribe(RideCancelledEvent.class, this::onRideCancelled);
        eventBus.subscribe(DriverFreedEvent.class, this::onDriverFreed);
    }
    @SneakyThrows
    private void save(Long rideId, String type, Object payload) {
        String json = payload == null ? null : mapper.writeValueAsString(payload);
        auditRepository.save(RideAuditEvent.builder()
                .rideId(rideId)
                .eventType(type)
                .payload(json)
                .createdAt(OffsetDateTime.now())
                .build());
        // Also log to console for dev
        System.out.printf("[AUDIT] ride=%d type=%s payload=%s%n", rideId, type, json);
    }
    private void onDriverAssigned(DriverAssignedEvent e) {
        record Payload(Long driverUserId) {}
        save(e.rideId(), e.type(), new Payload(e.driverUserId()));
    }
    private void onRideStarted(RideStartedEvent e) {
        record Payload(Long driverUserId) {}
        save(e.rideId(), e.type(), new Payload(e.driverUserId()));
    }
    private void onRideCompleted(RideCompletedEvent e) {
        record Payload(Long driverUserId, String fare, String currency) {}
        save(e.rideId(), e.type(), new Payload(e.driverUserId(), e.fare().toPlainString(), e.currency()));
    }
    private void onRideCancelled(RideCancelledEvent e) {
        record Payload(Long actorUserId) {}
        save(e.rideId(), e.type(), new Payload(e.actorUserId()));
    }
    private void onDriverFreed(DriverFreedEvent e) {
        record Payload(Long driverUserId) {}
        save(e.rideId(), e.type(), new Payload(e.driverUserId()));
    }

}
