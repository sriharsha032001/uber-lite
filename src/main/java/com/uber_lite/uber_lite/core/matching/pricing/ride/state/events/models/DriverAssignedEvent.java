package com.uber_lite.uber_lite.core.matching.pricing.ride.state.events.models;

import java.time.OffsetDateTime;

import com.uber_lite.uber_lite.core.matching.pricing.ride.state.events.DomainEvent;

public record DriverAssignedEvent(Long rideId, Long driverUserId, OffsetDateTime occurredAt) implements DomainEvent{
        @Override
        public String type() {
            return "Driver Assigned";
        }

}
