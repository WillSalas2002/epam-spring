package com.epam.spring.storage;

import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import com.epam.spring.model.User;
import com.epam.spring.util.UsernameGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

@Component
@PropertySource("classpath:application.properties")
@RequiredArgsConstructor
public class InMemoryStorage {

    private static final Logger LOGGER = Logger.getLogger(InMemoryStorage.class.getName());

    @Value("${storage.trainee.initial-data-path}")
    private String initialDataPath;

    private final Map<UUID, Trainee> traineeStorage;
    private final Map<UUID, Trainer> trainerStorage;
    private final Map<UUID, Training> trainingStorage;
    private final Set<String> usernameStorage;
    private final UsernameGenerator usernameGenerator;

    private final String TRAINEE_CLASS_NAME = "Trainee";
    private final String TRAINER_CLASS_NAME = "Trainer";
    private final String TRAINING_CLASS_NAME = "Training";
    private final String SEPARATOR = ",";

    @PostConstruct
    public void initializeStorages() {
        loadInitialData(initialDataPath);
    }

    public Map<UUID, Trainee> getTraineeStorage() {
        return traineeStorage;
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
            case TRAINEE_CLASS_NAME -> {
                Trainee trainee = buildTrainee(pieces);
                createTrainee(trainee);
            }
            case TRAINER_CLASS_NAME -> {
                Trainer trainer = buildTrainer(pieces);
                createTrainer(trainer);
            }
            case TRAINING_CLASS_NAME -> {
                Training training = buildTraining(pieces);
                createTraining(training);
            }
        }
    }


    private Trainee getTraineeByUsername(String piece) {
        List<Trainee> trainees = findAllTrainees();
        return trainees.stream()
                .filter(trainee -> trainee.getUsername().equals(piece))
                .findFirst().orElseThrow(() -> new NoSuchElementException("Trainer not found"));
    }

    private Trainer getTrainerByUsername(String piece) {
        return findAllTrainers().stream()
                .filter(trainee -> trainee.getUsername().equals(piece))
                .findFirst().orElseThrow(() -> new NoSuchElementException("Trainer not found"));
    }

    private Training buildTraining(String[] pieces) {
        Training training = new Training();
        training.setTrainee(getTraineeByUsername(pieces[1]));
        training.setTrainer(getTrainerByUsername(pieces[2]));
        training.setName(pieces[3]);
        training.setType(TrainingType.CROSS_FIT);
        training.setDate(LocalDateTime.parse(pieces[4]));
        training.setDuration(Integer.parseInt(pieces[5]));
        return training;
    }

    private Trainer buildTrainer(String[] pieces) {
        Trainer trainer = new Trainer();
        trainer.setFirstName(pieces[1]);
        trainer.setLastName(pieces[2]);
        trainer.setUsername(pieces[3]);
        trainer.setPassword(pieces[4]);
        trainer.setSpecialization(pieces[5]);
        trainer.setActive(pieces[6].equals(Boolean.TRUE.toString()));
        return trainer;
    }

    private Trainee buildTrainee(String[] pieces) {
        Trainee trainee = new Trainee();
        trainee.setFirstName(pieces[1]);
        trainee.setLastName(pieces[2]);
        trainee.setUsername(pieces[3]);
        trainee.setPassword(pieces[4]);
        trainee.setDataOfBirth(LocalDate.parse(pieces[5]));
        trainee.setAddress(pieces[6]);
        trainee.setActive(pieces[7].equals(Boolean.TRUE.toString()));
        return trainee;
    }

    // Methods related to Trainee
    public List<Trainee> findAllTrainees() {
        LOGGER.info("Fetching all trainees.");
        return traineeStorage.values().stream().toList();
    }

    public Trainee createTrainee(Trainee trainee) {
        LOGGER.info("Creating Trainee: " + trainee.getUsername());
        makeUsernameUnique(trainee);
        traineeStorage.put(trainee.getUuid(), trainee);
        return trainee;
    }

    public Trainee findTraineeById(UUID uuid) {
        LOGGER.info("Fetching Trainee with id: " + uuid);
        return traineeStorage.get(uuid);
    }

    public Trainee updateTrainee(Trainee trainee) {
        LOGGER.info("Updating Trainee with id: " + trainee.getUuid());
        makeUsernameUnique(trainee);
        traineeStorage.put(trainee.getUuid(), trainee);
        return trainee;
    }

    public void deleteTrainee(Trainee trainee) {
        LOGGER.info("Deleting Trainee with username: " + trainee.getUsername());
        usernameStorage.remove(trainee.getUsername());
        traineeStorage.remove(trainee.getUuid());
    }

    // Methods related to Trainer
    public List<Trainer> findAllTrainers() {
        LOGGER.info("Fetching all trainers.");
        return trainerStorage.values().stream().toList();
    }

    public Trainer createTrainer(Trainer trainer) {
        LOGGER.info("Creating Trainer: " + trainer.getUsername());
        makeUsernameUnique(trainer);
        trainerStorage.put(trainer.getUuid(), trainer);
        return trainer;
    }

    public Trainer findTrainerById(UUID uuid) {
        LOGGER.info("Fetching Trainer with id: " + uuid);
        return trainerStorage.get(uuid);
    }

    public Trainer updateTrainer(Trainer trainer) {
        LOGGER.info("Updating Trainer with id: " + trainer.getUuid());
        makeUsernameUnique(trainer);
        trainerStorage.put(trainer.getUuid(), trainer);
        return trainer;
    }

    public void deleteTrainer(Trainer trainer) {
        LOGGER.info("Deleting Trainee with username: " + trainer.getUsername());
        usernameStorage.remove(trainer.getUsername());
        trainerStorage.remove(trainer.getUuid());
    }

    // Methods related to Training
    public List<Training> findAllTrainings() {
        LOGGER.info("Fetching all trainings.");
        return trainingStorage.values().stream().toList();
    }

    public Training findTrainingById(UUID uuid) {
        LOGGER.info("Fetching Training with id: " + uuid);
        return trainingStorage.get(uuid);
    }

    public Training createTraining(Training training) {
        LOGGER.info("Creating Training: " + training.getName());
        trainingStorage.put(training.getUuid(), training);
        return training;
    }

    private void makeUsernameUnique(User user) {
        String username = usernameGenerator.generateUniqueUsername(user.getUsername(), usernameStorage);
        user.setUsername(username);
    }
}
