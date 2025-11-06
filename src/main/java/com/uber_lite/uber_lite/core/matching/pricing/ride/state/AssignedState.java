package com.uber_lite.uber_lite.core.matching.pricing.ride.state;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.uber_lite.uber_lite.domain.Ride;
import com.uber_lite.uber_lite.domain.RideStatus;
import com.uber_lite.uber_lite.domain.User;

public class AssignedState implements Ridestate{

    @Override
    public void assign(Ride ride, User diver){
        throw new IllegalStateException("Ride already assigned");
    }

    @Override
    public void started(Ride ride, Long driverId){
        if(ride.getStatus() !=  RideStatus.DRIVER_ASSIGNED){
            throw new IllegalStateException("Ride not in DRIVER_ASSIGNED state");
        }
        if (ride.getDriver() == null || !ride.getDriver().getId().equals(driverId)) throw new IllegalStateException("Wrong driver");

        ride.setStatus(RideStatus.STARTED);
        ride.setStartedAt(OffsetDateTime.now());
    }

        @Override 
        public void completed(Ride ride, Long driverId, BigDecimal fare) { throw new IllegalStateException("Cannot complete before start"); }

        @Override
        public void cancel(Ride ride, Long actorUserId) {
            ride.setStatus(RideStatus.CANCELED);


        }   
}
