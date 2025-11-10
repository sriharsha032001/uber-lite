package com.uber_lite.uber_lite.core.matching.pricing.ride.state;

import java.math.BigDecimal;

import com.uber_lite.uber_lite.domain.Ride;
import com.uber_lite.uber_lite.domain.User;

public class CompletedState implements Ridestate{

        @Override public void assign(Ride ride, User driver) { throw new IllegalStateException("Already completed"); }
        @Override public void started(Ride ride, Long driverId) { throw new IllegalStateException("Already completed"); }
        @Override public void completed(Ride ride, Long driverId, BigDecimal fare) { /* idempotent no-op allowed? */ }
        @Override public void cancel(Ride ride, Long actorUserId) { throw new IllegalStateException("Cannot cancel completed ride"); }




}
