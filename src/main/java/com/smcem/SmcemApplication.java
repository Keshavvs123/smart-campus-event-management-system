package com.smcem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmcemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmcemApplication.class, args);
    }

}