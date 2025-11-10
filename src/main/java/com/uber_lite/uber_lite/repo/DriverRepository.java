package com.uber_lite.uber_lite.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uber_lite.uber_lite.domain.Driver;
import com.uber_lite.uber_lite.domain.DriverStatus;

public interface DriverRepository extends JpaRepository<Driver, Long>{

    List<Driver> findByStatus(DriverStatus status);
    
}
