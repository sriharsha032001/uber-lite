package com.uber_lite.uber_lite.core.matching.pricing.ride.state.events;

import java.time.OffsetDateTime;

public interface DomainEvent {
    String type();
    OffsetDateTime occurredAt();
}
