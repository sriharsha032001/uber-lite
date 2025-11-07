package com.uber_lite.uber_lite.dto.ride;

import jakarta.validation.constraints.NotNull;

public record RideCancelDTO(
    @NotNull Long actorUserId
) {
    
}
