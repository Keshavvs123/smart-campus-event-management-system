package com.smcem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseFix implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("ALTER TABLE events MODIFY status VARCHAR(255)");
            System.out.println("=====================================================");
            System.out.println("FIXED STATUS COLUMN SCHEMA TO RESOLVE ENUM TRUNCATION!");
            System.out.println("=====================================================");
        } catch (Exception e) {
            System.out.println("Could not alter status column: " + e.getMessage());
        }
    }
}
