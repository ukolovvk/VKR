package ru.ssau.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author ukolov-victor
 */

@SpringBootApplication
@EnableDiscoveryClient
public class Backend {
    public static void main(String[] args) {
        SpringApplication.run(Backend.class, args);
    }
}