package com.uber_lite.uber_lite.core.matching.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import com.uber_lite.uber_lite.domain.Ride;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SurgeDecorator implements PricingStrategy {

        private final DistanceTimePricing base;   // wrap base pricing
        private final SurgeService surgeService;  // multiplier provider

        @Override
    public BigDecimal price(Ride ride) {
        BigDecimal baseFare = base.price(ride);
        double surge = surgeService.multiplierFor(ride.getPickupLat(), ride.getPickupLon());
        return baseFare.multiply(BigDecimal.valueOf(surge)).setScale(2, RoundingMode.HALF_UP);
    }

    
}
