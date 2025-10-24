package com.uber_lite.uber_lite.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uber_lite.uber_lite.domain.DriverLocation;

public interface DriverLocationRepository extends JpaRepository<DriverLocation, Long> {

    List<DriverLocation> findAll();
    
}
