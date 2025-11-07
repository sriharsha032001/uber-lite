package com.uber_lite.uber_lite.dto.ride;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RideRequestDTO(
    @NotNull Long riderId,
    @NotNull Double pickupLat,
    @NotNull Double pickupLon,
    @NotNull Double dropLat,
    @NotNull Double dropLon,
    @Pattern(regexp = "BIKE|MINI|SEDAN", message = "vehicleType must be BIKE, MINI or SEDAN")
    String vehicleType,

    String idempotencyKey
) {
    
}
