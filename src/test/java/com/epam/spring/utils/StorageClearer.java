package com.epam.spring.utils;

import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class StorageClearer {

    @Autowired
    private Map<Long, Trainee> traineeStorage;

    @Autowired
    private Map<Long, Trainer> trainerStorage;

    @Autowired
    private Map<Long, Training> trainingStorage;

    @Autowired
    private Set<String> usernameStorage;

    public void clear() {
        traineeStorage.clear();
        trainerStorage.clear();
        trainingStorage.clear();
        usernameStorage.clear();
    }
}
