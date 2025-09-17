package org.upyog.gis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot application class for utilities-gis microservice
 */
@SpringBootApplication
@EnableTransactionManagement
public class UtilitiesGisApplication {

    public static void main(String[] args) {
        SpringApplication.run(UtilitiesGisApplication.class, args);
    }
}
