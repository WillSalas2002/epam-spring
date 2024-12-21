package com.epam.spring.storage;

import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.User;
import com.epam.spring.service.TraineeService;
import com.epam.spring.service.TrainerService;
import com.epam.spring.service.TrainingService;
import com.epam.spring.util.EntityBuilder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

@Component
@PropertySource("classpath:application.properties")
@RequiredArgsConstructor
public class InMemoryStorage {

    private static final Logger LOGGER = Logger.getLogger(InMemoryStorage.class.getName());

    @Value("${storage.trainee.initial-data-path}")
    private String initialDataPath;

    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingService trainingService;

    private final String TRAINEE_CLASS_TYPE = "Trainee";
    private final String TRAINER_CLASS_TYPE = "Trainer";
    private final String TRAINING_CLASS_TYPE = "Training";
    private final String SEPARATOR = ",";

    @PostConstruct
    public void initializeStorages() {
        loadInitialData(initialDataPath);
    }

    public void loadInitialData(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            LOGGER.info("Initializing storage from file: " + initialDataPath);
            String line;
            while ((line = reader.readLine()) != null) {
                processDataLine(line);
            }
            LOGGER.info("Storage successfully initialized.");
        } catch (IOException e) {
            LOGGER.severe("Error initializing storage: " + e.getMessage());
        }
    }

    private void processDataLine(String line) {
        String[] pieces = line.split(SEPARATOR);
        String prefix = pieces[0];
        switch (prefix) {
            case TRAINEE_CLASS_TYPE -> {
                Trainee trainee = EntityBuilder.buildTrainee(pieces);
                createEntity(trainee);
            }
            case TRAINER_CLASS_TYPE -> {
                Trainer trainer = EntityBuilder.buildTrainer(pieces);
                createEntity(trainer);
            }
            case TRAINING_CLASS_TYPE -> {
                Trainee trainee = getUserByUsername(traineeService.findAll(), pieces[1], "Trainee + " + pieces[1] + " not found");
                Trainer trainer = getUserByUsername(trainerService.findAll(), pieces[2], "Trainer + " + pieces[2] + " not found");
                Training training = EntityBuilder.buildTraining(pieces, trainee, trainer);
                createEntity(training);
            }
        }
    }

    private <T> void createEntity(T entity) {
        if (entity instanceof Trainee) {
            traineeService.create((Trainee) entity);
        } else if (entity instanceof Trainer) {
            trainerService.create((Trainer) entity);
        } else if (entity instanceof Training) {
            trainingService.create((Training) entity);
        } else {
            throw new IllegalArgumentException("Unsupported data type " + entity.getClass().getName());
        }
    }

    private static <T extends User> T getUserByUsername(Collection<T> users, String username, String errorMessage) {
        return users.stream()
                .filter(trainee -> trainee.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(errorMessage));
    }
}
