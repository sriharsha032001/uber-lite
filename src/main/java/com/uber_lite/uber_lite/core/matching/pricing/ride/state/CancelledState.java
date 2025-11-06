package com.uber_lite.uber_lite.core.matching.pricing.ride.state;

import com.uber_lite.uber_lite.domain.Ride;
import com.uber_lite.uber_lite.domain.User;

public class CancelledState implements Ridestate {

    @Override
    public void assign(Ride ride, User driver) {
        throw new IllegalStateException("Ride is CANCELLED");
    }

    @Override
    public void started(Ride ride, Long driverId) {
        throw new IllegalStateException("Ride is CANCELLED");
    }

    @Override
    public void completed(Ride ride, Long driverId, BigDecimal fare) {
        throw new IllegalStateException("Ride is CANCELLED");
    }

    @Override
    public void cancel(Ride ride, Long actorUserId) {
    }
    
}
