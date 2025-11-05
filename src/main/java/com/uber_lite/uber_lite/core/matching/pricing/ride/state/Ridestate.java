package com.uber_lite.uber_lite.core.matching.pricing.ride.state;

import java.math.BigDecimal;

import com.uber_lite.uber_lite.domain.Ride;
import com.uber_lite.uber_lite.domain.User;

public interface Ridestate {

    void assign(Ride ride, User driver);
    void started(Ride ride, Long driverId);
    void completed(Ride ride , Long driverId, BigDecimal fare);
    void cancel(Ride ride, Long actorUserId);

}
