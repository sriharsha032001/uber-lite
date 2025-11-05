package com.uber_lite.uber_lite.core.matching.pricing;

import java.math.BigDecimal;

import com.uber_lite.uber_lite.domain.Ride;

public interface PricingStrategy {

    BigDecimal price(Ride ride);
    
}
