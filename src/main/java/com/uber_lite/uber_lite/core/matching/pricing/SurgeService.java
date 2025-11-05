package com.uber_lite.uber_lite.core.matching.pricing;

import org.springframework.stereotype.Service;

@Service
public class SurgeService {

    public double multiplierFor(double lat, double lon) {
        return 1.0; // No surge implemented yet
}
}
