package com.uber_lite.uber_lite.dto.ride;

import java.time.OffsetDateTime;

public record RideResponseDTO(

    Long id,
        Long riderId,
        Long driverUserId,
        double pickupLat,
        double pickupLon,
        double dropLat,
        double dropLon,
        String status,
        Double fareAmount,
        String currency,
        OffsetDateTime startedAt,
        OffsetDateTime endedAt

){}
