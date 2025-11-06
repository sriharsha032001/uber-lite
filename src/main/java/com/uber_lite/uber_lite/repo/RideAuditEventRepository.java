package com.uber_lite.uber_lite.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uber_lite.uber_lite.domain.RideAuditEvent;

public interface RideAuditEventRepository extends JpaRepository<RideAuditEvent, Long>  {
    
}
