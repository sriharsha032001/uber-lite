package com.uber_lite.uber_lite;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.uber_lite.uber_lite.domain.Role;
import com.uber_lite.uber_lite.domain.User;
import com.uber_lite.uber_lite.repo.UserRepository;

@SpringBootApplication
public class UberLiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(UberLiteApplication.class, args);

	}

	@Bean
CommandLineRunner seed(UserRepository users) {
    return args -> {
        if (users.count() == 0) {
            users.save(User.builder()
                    .name("Test Rider")
                    .phoneNumber("9999999999")
                    .role(Role.RIDER)
                    .build());
        }
        System.out.println("Users in DB: " + users.count());
    };
}

}
