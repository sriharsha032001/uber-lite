package com.uber_lite.uber_lite.core.matching;

import java.util.Optional;

import com.uber_lite.uber_lite.domain.Driver;

public interface DriverMatchingStrategy {
    Optional<Driver> match(double PickupLat, double PickupLon, String Vehicle);
    
}
