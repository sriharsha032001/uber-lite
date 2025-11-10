package com.uber_lite.uber_lite.domain;

import java.time.OffsetDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "driver_locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverLocation {

    @Id
    private Long driverId;

    private double latitude;
    private double longitude;

    private OffsetDateTime updatedAt;
}
