package com.uber_lite.uber_lite.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.uber_lite.uber_lite.core.matching.DriverMatchingStrategy;
import com.uber_lite.uber_lite.core.matching.pricing.PricingStrategy;
import com.uber_lite.uber_lite.core.matching.pricing.ride.state.RideStateFactory;
import com.uber_lite.uber_lite.core.matching.pricing.ride.state.events.EventBus;
import com.uber_lite.uber_lite.core.matching.pricing.ride.state.events.models.DriverAssignedEvent;
import com.uber_lite.uber_lite.core.matching.pricing.ride.state.events.models.DriverFreedEvent;
import com.uber_lite.uber_lite.core.matching.pricing.ride.state.events.models.RideCancelledEvent;
import com.uber_lite.uber_lite.core.matching.pricing.ride.state.events.models.RideCompletedEvent;
import com.uber_lite.uber_lite.core.matching.pricing.ride.state.events.models.RideStartedEvent;
import com.uber_lite.uber_lite.domain.Driver;
import com.uber_lite.uber_lite.domain.DriverStatus;
import com.uber_lite.uber_lite.domain.IdempotencyKey;
import com.uber_lite.uber_lite.domain.Ride;
import com.uber_lite.uber_lite.domain.RideStatus;
import com.uber_lite.uber_lite.domain.User;
import com.uber_lite.uber_lite.repo.DriverRepository;
import com.uber_lite.uber_lite.repo.IdempotencyKeyRepository;
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
    private final RideStateFactory stateFactory;
    private final EventBus eventBus;
    private final IdempotencyKeyRepository idempotencyKeyRepository;

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

            tryAssignDriverWithRetries(ride.getId(), pickupLat, pickupLon, vehicleType, 3);

        return ride;
    }

    public void tryAssignDriverWithRetries(Long rideId, double pLat, double pLon, String vehicleType, int retries){

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
         var state = stateFactory.from(rider.getStatus());
         state.assign(rider , candidate.getUser());
        rider.setDriver(candidate.getUser());

        candidate.setStatus(DriverStatus.ASSIGNED);

         rideRepository.save(rider);
        driverRepository.save(candidate);

        eventBus.publish(new DriverAssignedEvent(
        rider.getId(),
        candidate.getUser().getId(),
        java.time.OffsetDateTime.now()
    ));

        return true;

}
      @Transactional
      public void startRide(Long rideId, Long driverId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow();
        
        var state = stateFactory.from(ride.getStatus());
        state.started(ride, driverId);

        ride.setStartedAt(OffsetDateTime.now());
        rideRepository.save(ride);

        eventBus.publish(new RideStartedEvent(
        ride.getId(),
        ride.getDriver().getId(),
        java.time.OffsetDateTime.now()
    ));

      }

      @Transactional
        public BigDecimal completeRide(Long rideId, Long driverId) {
                Ride ride = rideRepository.findById(rideId).orElseThrow();

    BigDecimal fare = pricingStrategy.price(ride);

    var state = stateFactory.from(ride.getStatus());
    state.completed(ride, driverId, fare);

    if (ride.getCurrency() == null) ride.setCurrency("INR");
    if (ride.getEndedAt() == null) ride.setEndedAt(OffsetDateTime.now());

    rideRepository.save(ride);

    freeDriverAfterRide(ride); // free driver after ride completion

    eventBus.publish(new RideCompletedEvent(
        ride.getId(),
        ride.getDriver().getId(),
        fare,
        ride.getCurrency(),
        java.time.OffsetDateTime.now()
    ));

    return fare;


}
        @Transactional
        public void cancelRide(Long rideId, Long actorUserId) {
         var ride = rideRepository.findById(rideId).orElseThrow();
         var state = stateFactory.from(ride.getStatus());
         state.cancel(ride, actorUserId);
         rideRepository.save(ride);

         freeDriverAfterRide(ride);

         eventBus.publish(new RideCancelledEvent(
        ride.getId(),
        actorUserId,
        java.time.OffsetDateTime.now()
));
}
        public void freeDriverAfterRide(Ride ride){
            if(ride.getStatus() == RideStatus.COMPLETED || ride.getStatus() == RideStatus.CANCELED || ride.getDriver() != null){

                driverRepository.findById(ride.getDriver().getId()).ifPresent(
                    d -> {
                        d.setStatus(DriverStatus.IDLE);
                        driverRepository.save(d);

                        eventBus.publish(new DriverFreedEvent(
                        ride.getId(),
                         d.getUser().getId(),
                      java.time.OffsetDateTime.now()
            ));

                    }
                );

                

                }
            }
            public Ride getById(Long rideId) {
                return rideRepository.findById(rideId).orElseThrow();
            }

            public Ride requestRideWithIdempotency(RideRequestInputs inputs, String idemKey) {
                if (idemKey == null || idemKey.isBlank()) {
            // fallback to normal behavior
            return requestRide(inputs.riderId(), inputs.pickupLat(), inputs.pickupLon(),
                               inputs.dropLat(), inputs.dropLon(), inputs.vehicleType());
            }
            final String key = idemKey.trim();

            try {
            IdempotencyKey newKey = IdempotencyKey.builder()
                    .key(key)
                    .status("IN_PROGRESS")
                    .createdAt(OffsetDateTime.now())
                    .updatedAt(OffsetDateTime.now())
                    .build();
            // save should fail if key exists (PK)
            idempotencyKeyRepository.save(newKey);
            // we "won" the race — now create the ride and update the row
            Ride ride = requestRide(inputs.riderId(), inputs.pickupLat(), inputs.pickupLon(),
                    inputs.dropLat(), inputs.dropLon(), inputs.vehicleType());

            // update idempotency record
            newKey.setRideId(ride.getId());
            newKey.setStatus("COMPLETED");
            newKey.setUpdatedAt(OffsetDateTime.now());
            idempotencyKeyRepository.save(newKey);

            return ride;
                }
                catch (DataIntegrityViolationException ex) {
            // someone else inserted that key before us -> fetch it
            return resolveExistingIdempotency(key);
        }
    }

    private Ride resolveExistingIdempotency(String key) {
        final Duration maxWait = Duration.ofSeconds(5);
        final long pollIntervalMs = 100L;
        final OffsetDateTime start = OffsetDateTime.now();
        while (Duration.between(start, OffsetDateTime.now()).compareTo(maxWait) < 0) {
            IdempotencyKey rec = idempotencyKeyRepository.findById(key).orElse(null);
            if (rec == null) {
                // Strange: not found — try again quickly
                try { Thread.sleep(pollIntervalMs); } catch (InterruptedException ignored) {}
                continue;
            }
            if (rec.getRideId() != null && "COMPLETED".equals(rec.getStatus())) {
                // success: fetch and return ride
                return rideRepository.findById(rec.getRideId()).orElseThrow();
            }
            // still in progress: wait a bit
            try { Thread.sleep(pollIntervalMs); } catch (InterruptedException ignored) {}
        }
        // timed out waiting — defensive approach: fetch current record; if has rideId return it, else raise
        IdempotencyKey rec = idempotencyKeyRepository.findById(key).orElse(null);
        if (rec != null && rec.getRideId() != null) {
            return rideRepository.findById(rec.getRideId()).orElseThrow();
        }
        // If still no ride id, we give up: throw an error or return a 202-like response.
        throw new IllegalStateException("Idempotent request in progress. Try again later");
    }

    public static record RideRequestInputs(Long riderId, Double pickupLat, Double pickupLon,
                                           Double dropLat, Double dropLon, String vehicleType) {}
}

