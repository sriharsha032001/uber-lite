package com.uber_lite.uber_lite.domain;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "idempotency_keys")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdempotencyKey {

    @Id
    @Column(name = "key_text" , nullable = false)
    private String key;

    @Column(name = "ride_id")
    private Long rideId;

    @Column(name = "status", nullable = false)
    private String status; // "IN_PROGRESS" or "COMPLETED"

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    
}
