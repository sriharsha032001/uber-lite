package com.uber_lite.uber_lite.dto.ride;

import com.uber_lite.uber_lite.domain.Ride;

public class RideMapper {

    private RideMapper() { }

    public static RideResponseDTO toDto(Ride r) {

        return new RideResponseDTO(
                r.getId(),
                r.getRider() != null ? r.getRider().getId() : null,
                r.getDriver() != null ? r.getDriver().getId() : null,
                r.getPickupLat(),
                r.getPickupLon(),
                r.getDropLat(),
                r.getDropLon(),
                r.getStatus() != null ? r.getStatus().name() : null,
                r.getFareAmount(),
                r.getCurrency(),
                r.getStartedAt(),
                r.getEndedAt()
        );
    }
    
}
