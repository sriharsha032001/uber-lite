package com.uber_lite.uber_lite.core.matching.pricing.ride.state.events.models;

import java.time.OffsetDateTime;

import com.uber_lite.uber_lite.core.matching.pricing.ride.state.events.DomainEvent;

public record DriverFreedEvent(Long rideId, Long driverUserId, OffsetDateTime occurredAt) implements DomainEvent {

    @Override
    public String type() {
        return "Driver Freed";
    }
    
}
