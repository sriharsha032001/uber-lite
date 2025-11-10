package com.uber_lite.uber_lite.core.matching.pricing.ride.state.events.models;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.uber_lite.uber_lite.core.matching.pricing.ride.state.events.DomainEvent;

public record RideCompletedEvent(Long rideId, Long driverUserId, BigDecimal fare, String currency,
                                 OffsetDateTime occurredAt) implements DomainEvent {
                            
                @Override
                public String type(){
                    return "Ride completed";
                     }
 }
                                    
    

