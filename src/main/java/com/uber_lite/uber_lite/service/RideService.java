package com.uber_lite.uber_lite.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.uber_lite.uber_lite.core.matching.DriverMatchingStrategy;
import com.uber_lite.uber_lite.core.matching.pricing.PricingStrategy;
import com.uber_lite.uber_lite.domain.Driver;
import com.uber_lite.uber_lite.domain.DriverStatus;
import com.uber_lite.uber_lite.domain.Ride;
import com.uber_lite.uber_lite.domain.RideStatus;
import com.uber_lite.uber_lite.domain.User;
import com.uber_lite.uber_lite.repo.DriverRepository;
import com.uber_lite.uber_lite.repo.RideRepository;
import com.uber_lite.uber_lite.repo.UserRepository;

import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final DriverMatchingStrategy driverMatchingStrategy;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final PricingStrategy pricingStrategy;

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

            tryAssignDriverWithRetires(ride.getId(), pickupLat, pickupLon, vehicleType, 3);

        return ride;
    }

    public void tryAssignDriverWithRetires(Long rideId, double pLat, double pLon, String vehicleType, int retries){

        int attempts = 0;

        while(attempts < retries){
            attempts++;
            try{
                boolean ok = assignDriverOnce(rideId, pLat , pLon , vehicleType);
                if(ok)
                    return;

                    break;
            }
             catch (OptimisticLockException | DataAccessException ex){
                try {
                    Thread.sleep(50L * attempts);
                }
                catch (InterruptedException ignored){
                }
            }
        }
    }

    @Transactional
    public boolean assignDriverOnce(Long rideId, double pLat, double pLon, String vehicleType) {

        Ride rider = rideRepository.findById(rideId).orElseThrow();

        if(rider.getStatus() != RideStatus.REQUESTED) {
            return true;  // someone already is assigneds
        }

        // Pick a candidate (based on latest locations)
        Optional<Driver> candidateOpt = driverMatchingStrategy.match(pLat, pLon, vehicleType);
        if (candidateOpt.isEmpty()) return false;

        Driver candidate = driverRepository.findById(candidateOpt.get().getId())
                .orElseThrow(); // reload within this tx

        if (candidate.getStatus() != DriverStatus.IDLE) {
            // someone else took them; let caller retry with another candidate
            throw new OptimisticLockException("Driver not idle anymore");
        }
        // Assign driver to ride
         // Perform atomic state changes
        rider.setDriver(candidate.getUser());
        rider.setStatus(RideStatus.DRIVER_ASSIGNED);
        candidate.setStatus(DriverStatus.ASSIGNED);

         rideRepository.save(rider);
        driverRepository.save(candidate);

        return true;

}
      @Transactional
      public void startRide(Long rideId, Long driverId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow();
        if(ride.getDriver() == null || !ride.getDriver().getId().equals(driverId)) {
            throw new IllegalArgumentException("Driver not assigned to this ride");
        }
        if (ride.getStatus() != RideStatus.DRIVER_ASSIGNED) {
        throw new IllegalStateException("Ride is not in DRIVER_ASSIGNED state");
    }

        ride.setStatus(RideStatus.STARTED);
        ride.setStartedAt(OffsetDateTime.now());
        rideRepository.save(ride);

      }

      @Transactional
        public BigDecimal completeRide(Long rideId, Long driverId) {
                Ride ride = rideRepository.findById(rideId).orElseThrow();

                if (ride.getDriver() == null || !ride.getDriver().getId().equals(driverId)) {
             throw new IllegalStateException("Driver not assigned to this ride");
    }

    if (ride.getStatus() != RideStatus.STARTED) {
        throw new IllegalStateException("Ride is not STARTED");
    }

    BigDecimal fare = pricingStrategy.price(ride);

    ride.setFareAmount(fare.doubleValue());
    ride.setCurrency("INR");
    ride.setStatus(RideStatus.COMPLETED);
    ride.setEndedAt(OffsetDateTime.now());
    rideRepository.save(ride);

    return fare;


}
}
