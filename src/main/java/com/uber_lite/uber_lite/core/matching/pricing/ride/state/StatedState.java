package com.uber_lite.uber_lite.core.matching.pricing.ride.state;

import java.time.OffsetDateTime;

import com.uber_lite.uber_lite.domain.Ride;
import com.uber_lite.uber_lite.domain.RideStatus;
import com.uber_lite.uber_lite.domain.User;

public class StatedState implements Ridestate{

    @Override
    public void assign(Ride ride, User driver) {
        throw new IllegalStateException("Ride already started");    
    }
    @Override
    public void started(Ride ride, Long driverId) {
        throw new IllegalStateException("Ride already started");
    }
    @Override
    public void completed(Ride ride, Long driverId, java.math.BigDecimal fare) {
         if (ride.getStatus() != RideStatus.STARTED) throw new IllegalStateException("Not STARTED");
         ride.setFareAmount(fare.doubleValue());
         ride.setStatus(RideStatus.COMPLETED);
         ride.setEndedAt(OffsetDateTime.now());
         if (ride.getCurrency() == null) ride.setCurrency("INR");
    }
    @Override public void cancel(Ride ride, Long actorUserId) {
        // Optional rule: disallow rider cancel after start; allow system/driver? For now allow.
        ride.setStatus(RideStatus.CANCELED);
        ride.setEndedAt(OffsetDateTime.now());
    }

}
