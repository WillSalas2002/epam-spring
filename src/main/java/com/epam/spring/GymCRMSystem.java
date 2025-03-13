package com.epam.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableJpaRepositories("com.epam.spring.repository")
@EntityScan("com.epam.spring.model")
public class GymCRMSystem {
    public static void main(String[] args) {
        SpringApplication.run(GymCRMSystem.class, args);
    }
}
