package com.uber_lite.uber_lite.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uber_lite.uber_lite.domain.Ride;
import com.uber_lite.uber_lite.domain.RideStatus;

public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByStatus(RideStatus status);
    
}
