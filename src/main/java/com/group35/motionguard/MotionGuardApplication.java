package com.group35.motionguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class MotionGuardApplication {

    public static void main(String[] args) {
        SpringApplication.run(MotionGuardApplication.class, args);
    }
}
