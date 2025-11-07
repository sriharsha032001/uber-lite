package com.uber_lite.uber_lite.web;
import java.time.OffsetDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uber_lite.uber_lite.domain.DriverLocation;
import com.uber_lite.uber_lite.domain.DriverStatus;
import com.uber_lite.uber_lite.dto.ride.driver.DriverLocationDTO;
import com.uber_lite.uber_lite.repo.DriverLocationRepository;
import com.uber_lite.uber_lite.repo.DriverRepository;
// import com.uber_lite.uber_lite.repo.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController { 

    // private final UserRepository userRepo;
    private final DriverRepository driverRepo;
    private final DriverLocationRepository locRepo;

    @PostMapping("/{driverId}/online")
    // Logic to set driver online
    public ResponseEntity<?> goOnline(@PathVariable Long driverId) {
        // Implementation here
        var d = driverRepo.findById(driverId).orElse(null);
        if(d == null) {
            return ResponseEntity.notFound().build();
        }
        d.setStatus(DriverStatus.IDLE);
        driverRepo.save(d);
        return ResponseEntity.ok().build();
    }   

    @PostMapping("/{driverId}/offline")
    // Logic to set driver offline
    public ResponseEntity<?> goOffline(@PathVariable Long driverId) {
        // Implementation here
        var d = driverRepo.findById(driverId).orElse(null);
        if(d == null) {
            return ResponseEntity.notFound().build();
        }
        d.setStatus(DriverStatus.OFFLINE);
        driverRepo.save(d);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{driverId}/location")
    // Logic to update driver location
    public ResponseEntity<?> updateLocation(@PathVariable Long driverId, @Valid @RequestParam DriverLocationDTO locationDto) {
        if(!driverRepo.existsById(driverId)) {
            return ResponseEntity.notFound().build();
        }   
        var location = DriverLocation.builder()
                .driverId(driverId)
                .latitude(locationDto.lat())
                .longitude(locationDto.lon())
                .updatedAt(OffsetDateTime.now())
                .build();
                locRepo.save(location);
                return ResponseEntity.ok().build();
    }

}
