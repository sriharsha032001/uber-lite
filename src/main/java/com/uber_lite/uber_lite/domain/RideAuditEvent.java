package com.uber_lite.uber_lite.domain;

import java.time.OffsetDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ride_audit_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideAuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long rideId;
    private String eventType;

    @Column(columnDefinition = "text")
    private String payload;
    
    private OffsetDateTime createdAt;

}
