package com.uber_lite.uber_lite.core.matching.pricing.ride.state;

import com.uber_lite.uber_lite.domain.RideStatus;

public class RideStateFactory {

    private final RequestedState requested = new RequestedState();
    private final AssignedState assigned = new AssignedState();
    private final StatedState started = new StatedState();
    private final CompletedState completed = new CompletedState();
    private final CancelledState cancelled = new CancelledState();

    public Ridestate from(RideStatus status) {
        return switch (status) {
            case REQUESTED -> requested;
            case DRIVER_ASSIGNED -> assigned;
            case STARTED -> started;
            case COMPLETED -> completed;
            case CANCELED -> cancelled;
        };

    }
    
}
