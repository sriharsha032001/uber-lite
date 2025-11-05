package com.uber_lite.uber_lite.web;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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

    @PostMapping("/{rideId}/start")
    public ResponseEntity<?> startRide(@RequestParam Long rideId , @RequestParam Long driverId) {
        rideService.startRide(rideId, driverId);
    return ResponseEntity.ok().build();
}

    @PostMapping("/{rideId}/complete")
    public ResponseEntity<Map<String, Object>> completeRide(@RequestParam Long rideId , @RequestParam Long driverId) {
         BigDecimal fare = rideService.completeRide(rideId, driverId);

    // 2) build a response map
    Map<String, Object> resp = new HashMap<>();
    resp.put("rideId", rideId);
    resp.put("fare", fare);          // BigDecimal is fine here
    resp.put("currency", "INR");

    // 3) return
    return ResponseEntity.ok(resp);
}
}
