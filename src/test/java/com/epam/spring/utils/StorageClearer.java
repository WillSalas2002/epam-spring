package com.epam.spring.utils;

import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class StorageClearer {

    @Autowired
    private Map<UUID, Trainee> traineeStorage;

    @Autowired
    private Map<UUID, Trainer> trainerStorage;

    @Autowired
    private Map<UUID, Training> trainingStorage;

    @Autowired
    private Set<String> usernameStorage;

    public void clear() {
        traineeStorage.clear();
        trainerStorage.clear();
        trainingStorage.clear();
        usernameStorage.clear();
    }
}
