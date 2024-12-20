package com.epam.spring.config;

import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Configuration
@ComponentScan("com.epam.spring")
public class AppConfig {

    @Bean
    public Map<UUID, Trainee> traineeStorage() {
        return new HashMap<>();
    }

    @Bean
    public Map<UUID, Trainer> trainerStorage() {
        return new HashMap<>();
    }

    @Bean
    public Map<UUID, Training> trainingStorage() {
        return new HashMap<>();
    }

    @Bean
    public Set<String> usernameStorage() {
        return new HashSet<>();
    }
}
