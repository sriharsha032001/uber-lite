package com.uber_lite.uber_lite.web;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uber_lite.uber_lite.domain.Ride;
import com.uber_lite.uber_lite.dto.ride.RideCancelDTO;
import com.uber_lite.uber_lite.dto.ride.RideCompleteDTO;
import com.uber_lite.uber_lite.dto.ride.RideMapper;
import com.uber_lite.uber_lite.dto.ride.RideRequestDTO;
import com.uber_lite.uber_lite.dto.ride.RideResponseDTO;
import com.uber_lite.uber_lite.dto.ride.RideStartDTO;
import com.uber_lite.uber_lite.ratelimit.RateLimiterService;
import com.uber_lite.uber_lite.service.RideService;
import com.uber_lite.uber_lite.service.RideService.RideRequestInputs;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import static com.uber_lite.uber_lite.dto.ride.RideMapper.toDto;


@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;
    private final RateLimiterService rateLimiter;


    @PostMapping
    public ResponseEntity<RideResponseDTO> request(@Valid @RequestBody RideRequestDTO req) {
       Ride r = rideService.requestRide(req.riderId(),req.pickupLat(), req.pickupLon(), req.dropLat(), req.dropLon(), req.vehicleType());
       return ResponseEntity.ok(toDto(r));                                         
    
}

    @PostMapping("/{rideId}/start")
    public ResponseEntity<RideResponseDTO> startRide(@PathVariable Long rideId,
                                                 @Valid @RequestBody RideStartDTO req) {
        rideService.startRide(rideId, req.driverId());

        Ride r = rideService.getById(rideId);
        return ResponseEntity.ok(toDto(r));
}

    @PostMapping("/{rideId}/complete")
    public ResponseEntity<RideResponseDTO> completeRide(@PathVariable Long rideId,
                                                    @Valid @RequestBody RideCompleteDTO req) {
         BigDecimal fare = rideService.completeRide(rideId, req.driverId());

        Ride r = rideService.getById(rideId);

        // 3) return
        return ResponseEntity.ok(toDto(r));
    }
    @PostMapping("/{rideId}/cancel")
    public ResponseEntity<RideResponseDTO> cancelRide(@PathVariable Long rideId , @Valid @RequestBody RideCancelDTO req) {
        rideService.cancelRide(rideId, req.actorUserId());
        Ride r = rideService.getById(rideId);
        return ResponseEntity.ok(toDto(r));

    }

        @PostMapping
        public ResponseEntity<RideResponseDTO> request(@Valid @RequestBody RideRequestDTO req,
                                               @RequestHeader(value = "Idempotency-Key", required = false) String idemKey) {

            // rate limit check
        if (!rateLimiter.allowRequest(req.riderId())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(null); // or return a nice error DTO
        }
        
        RideRequestInputs inputs = new RideRequestInputs(
            req.riderId(), req.pickupLat(), req.pickupLon(),
            req.dropLat(), req.dropLon(), req.vehicleType()
        );
            Ride r = rideService.requestRideWithIdempotency(inputs, idemKey);
            return ResponseEntity.ok(RideMapper.toDto(r));
        }

}
