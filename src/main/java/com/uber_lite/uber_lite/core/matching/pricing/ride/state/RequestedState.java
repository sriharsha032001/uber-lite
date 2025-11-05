package com.uber_lite.uber_lite.core.matching.pricing.ride.state;

import java.math.BigDecimal;

import com.uber_lite.uber_lite.domain.Ride;
import com.uber_lite.uber_lite.domain.RideStatus;
import com.uber_lite.uber_lite.domain.User;

public class RequestedState implements Ridestate {

    @Override
    public void assign(Ride ride, User driver) {
        if(ride.getStatus() != RideStatus.REQUESTED) 
            throw new IllegalStateException("Not REQUESTED");

        ride.setDriver(driver);
        ride.setStatus(RideStatus.DRIVER_ASSIGNED);
    }
    @Override
    public void started(Ride ride, Long driverId) { 
        throw new IllegalStateException("Cannot start before assign"); 
    }

    @Override
    public void completed(Ride ride, Long driverId, BigDecimal fare) {
        throw new IllegalStateException("Cannot complete ride before starting");
    }

    @Override
    public void cancel(Ride ride, Long actorUserId) {
        if(ride.getStatus() != RideStatus.REQUESTED){
            throw new IllegalStateException("Cannot cancel ride not in REQUESTED state");
        }
                    ride.setStatus(RideStatus.CANCELED);



    }

}