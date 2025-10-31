package com.uber_lite.uber_lite.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.uber_lite.uber_lite.core.matching.DriverMatchingStrategy;
import com.uber_lite.uber_lite.domain.Driver;
import com.uber_lite.uber_lite.domain.Ride;
import com.uber_lite.uber_lite.domain.RideStatus;
import com.uber_lite.uber_lite.domain.User;
import com.uber_lite.uber_lite.repo.RideRepository;
import com.uber_lite.uber_lite.repo.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RideService {

    private RideRepository rideRepository;
    private DriverMatchingStrategy driverMatchingStrategy;
    private UserRepository userRepository;

    @Transactional
        public Ride requestRide(Long riderId, double pickupLat, double pickupLon, double dropLat, double dropLon, String vehicleType) {

            User rider = userRepository.findById(riderId).orElseThrow(() -> new IllegalArgumentException("Rider not found"));
            Ride ride = Ride.builder()
            .rider(rider)
            .PickupLat(pickupLat)
            .PickupLon(pickupLon)
            .DropLat(dropLat)
            .DropLon(dropLon)
            .status(RideStatus.REQUESTED)
            .currency("INR")
            .build();

            ride = rideRepository.save(ride);

            Optional<Driver> candidate = driverMatchingStrategy.match(pickupLat, pickupLon, vehicleType);
        candidate.ifPresent(driver -> {

            System.out.println("Found candidate driverId=" + driver.getId());
        });

        return ride;
    }


    
}
