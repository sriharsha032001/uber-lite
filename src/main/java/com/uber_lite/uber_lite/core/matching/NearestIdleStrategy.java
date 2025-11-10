package com.uber_lite.uber_lite.core.matching;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.uber_lite.uber_lite.domain.Driver;
import com.uber_lite.uber_lite.domain.DriverLocation;
import com.uber_lite.uber_lite.domain.DriverStatus;
import com.uber_lite.uber_lite.domain.Type;
import com.uber_lite.uber_lite.repo.DriverLocationRepository;
import com.uber_lite.uber_lite.repo.DriverRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NearestIdleStrategy implements DriverMatchingStrategy {

    private final DriverLocationRepository locRepo;
    private final DriverRepository driverRepo; 

    @Override
    public Optional<Driver> match(double PickupLat, double PickupLon, String Vehicletype) {

        // Implementation for finding nearest idle driver
        List<DriverLocation> location = locRepo.findAll();

        return location.stream()
        .map(dl -> driverRepo.findById(dl.getDriverId()). orElse(null))
        .filter(d -> d != null && d.getStatus() == DriverStatus.IDLE)
        .filter(d ->{
            if(Vehicletype == null) return true;
            try{
                Type vt = Type.valueOf(Vehicletype);
                return d.getVehicle() != null && d.getVehicle().getType() == vt;
            } catch (IllegalArgumentException e) {
                return true;
            }
        })
        .min(Comparator.comparingDouble(d -> {
            DriverLocation l = location.stream().filter(x -> x.getDriverId().equals(d.getId())).findFirst().orElse(null);
            if(l == null) return Double.MAX_VALUE;
            return haversineKm(PickupLat, PickupLon, l.getLatitude(), l.getLongitude());
        }));
    }
    private double haversineKm(double lat1, double lon1, double lat2, double lon2){

        final int R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;

    }
    
}
