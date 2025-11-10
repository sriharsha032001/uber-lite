package com.uber_lite.uber_lite.core.matching.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.uber_lite.uber_lite.domain.Ride;

@Service
public class DistanceTimePricing implements PricingStrategy {

    @Override
    public BigDecimal price(Ride ride) {
        // Implement distance and time based pricing logic here

        double km = haversineKm(
            ride.getPickupLat(), ride.getPickupLon(),
            ride.getDropLat(), ride.getDropLon()

        );
         double base = 30.0;       // base fee
        double perKm = 10.0;      // per-km cost
        double estimate = base + (perKm * km);

        return BigDecimal.valueOf(estimate).setScale(2, RoundingMode.HALF_UP); // Rounding Up to avoid erros in pricing
        
    }

     private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;

    }



}
