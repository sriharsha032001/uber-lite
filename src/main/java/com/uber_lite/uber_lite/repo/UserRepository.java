package com.uber_lite.uber_lite.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uber_lite.uber_lite.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhoneNumber(String phoneNumber);
    
}
