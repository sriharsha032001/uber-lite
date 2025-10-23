package com.uber_lite.uber_lite.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uber_lite.uber_lite.domain.Type;
import com.uber_lite.uber_lite.domain.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByPlate(String plate);
    List<Vehicle> findByType(Type type); 
    

    }