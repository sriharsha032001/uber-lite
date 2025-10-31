package com.uber_lite.uber_lite.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uber_lite.uber_lite.domain.Ride;
import com.uber_lite.uber_lite.service.RideService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideController {

    private RideService rideService;

    @PostMapping
    public ResponseEntity<Ride> request(@RequestParam Long rideId,
                                        @RequestParam double pickupLat,
                                        @RequestParam double pickupLon,
                                        @RequestParam double dropLat,
                                        @RequestParam double dropLon,
                                        @RequestParam(required = false) String VehicleType) {
       Ride r = rideService.requestRide(rideId, pickupLat, pickupLon, dropLat, dropLon, VehicleType);
       return ResponseEntity.ok(r);                                         
    
}
}
