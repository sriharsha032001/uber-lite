package com.uber_lite.uber_lite.dto.ride.driver;

import jakarta.validation.constraints.NotNull;

public record DriverLocationDTO(
    @NotNull Double lat,
    @NotNull Double lon
) {
    
}
