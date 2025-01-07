package com.epam.spring.facade;

import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.service.TraineeService;
import com.epam.spring.service.TrainerService;
import com.epam.spring.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class GymCrmFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymCrmFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    // ------------- TRAINEE OPERATIONS -----------------
    public List<Trainee> findAllTrainees() {
        log.info("Fetching all trainees.");
        List<Trainee> trainees = traineeService.findAll();
        log.info("Found {} trainees.", trainees.size());
        return trainees;
    }

    public Trainee createTrainee(Trainee trainee) {
        log.info("Creating Trainee with name: {}", trainee.getFirstName());
        Trainee createdTrainee = traineeService.create(trainee);
        log.info("Trainee created: {}", createdTrainee);
        return createdTrainee;
    }

    public boolean authenticateTrainee(String username, String password) {
        log.info("Received request for trainee authentication");
        boolean authenticate = traineeService.authenticate(username, password);
        log.info("Trainee authentication for inputs, username: {} and password: {}, returned: {}", username, password, authenticate);
        return authenticate;
    }

    public Trainee findTraineeByUsername(String username) {
        log.info("Received request for getting trainee by username: {}", username);
        Trainee trainee = traineeService.findByUsername(username);
        log.info("Trainee by username returned result: {}", trainee);
        return trainee;
    }

    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        log.info("Received request for password change for trainee with username: {}", username);
        traineeService.changePassword(username, oldPassword, newPassword);
    }

    public void activateTraineeProfile(Long id) {
        Trainee trainee = traineeService.findById(id);
        log.info("Received request for activating trainee {}", trainee);
        traineeService.activate(trainee);
        boolean isActive = trainee.isActive();
        log.info("{}'s status changed from {} to {}", trainee.getUsername(), !isActive, isActive);
    }

    public void findTraineesTrainingListByCriteria(String username, LocalDate fromDate, LocalDate toDate, String trainerFirstName, String trainingType) {
        log.info("Received request for getting traineeTrainings by criteria, {}, {}, {}, {}, {}", username, fromDate, toDate, trainerFirstName, trainingType);
        List<Training> traineeTrainings = trainingService.findTraineeTrainings(username, fromDate, toDate, trainerFirstName, trainingType);
        log.info("List of training found: {}", traineeTrainings);
    }

    public Trainee findTraineeById(Long id) {
        log.info("Fetching Trainee by ID: {}", id);
        Trainee trainee = traineeService.findById(id);
        if (trainee != null) {
            log.info("Trainee found: {}", trainee.getUsername());
        } else {
            log.warn("Trainee not found with ID: {}", id);
        }
        return trainee;
    }

    public Trainee updateTrainee(Trainee trainee) {
        log.info("Updating Trainee with ID: {}", trainee.getId());
        Trainee updatedTrainee = traineeService.update(trainee);
        log.info("Trainee updated: {}", updatedTrainee.getUsername());
        return updatedTrainee;
    }

    public void deleteTrainee(Trainee trainee) {
        log.info("Deleting Trainee with ID: {}", trainee.getId());
        traineeService.delete(trainee);
        log.info("Trainee deleted: {}", trainee.getUsername());
    }

    public void deleteTraineeByUsername(String username) {
        log.info("Received request for delete trainee by username: {}", username);
        traineeService.deleteByUsername(username);
    }

    // ------------- TRAINER OPERATIONS -----------------
    public List<Trainer> findAllTrainers() {
        log.info("Fetching all trainers.");
        List<Trainer> trainers = trainerService.findAll();
        log.info("Found {} trainers.", trainers.size());
        return trainers;
    }

    public Trainer createTrainer(Trainer trainer) {
        log.info("Creating Trainer with name: {}", trainer.getFirstName());
        Trainer createdTrainer = trainerService.create(trainer);
        log.info("Trainer created: {}", createdTrainer);
        return createdTrainer;
    }

    public boolean authenticateTrainer(String username, String password) {
        log.info("Received request for trainer authentication");
        boolean authenticate = trainerService.authenticate(username, password);
        log.info("Trainer authentication for inputs, username: {} and password: {}, returned: {}", username, password, authenticate);
        return authenticate;
    }

    public Trainer findTrainerByUsername(String username) {
        log.info("Received request for getting trainer by username: {}", username);
        Trainer trainer = trainerService.findByUsername(username);
        log.info("Trainer by username returned result: {}", trainer);
        return trainer;
    }

    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        log.info("Received request for password change for trainer with username: {}", username);
        trainerService.changePassword(username, oldPassword, newPassword);
    }

    public void activateTrainerProfile(Long id) {
        Trainer trainer = trainerService.findById(id);
        log.info("Received request for activating trainer {}", trainer);
        trainerService.activate(trainer);
        boolean isActive = trainer.isActive();
        log.info("{}'s status changed from {} to {}", trainer.getUsername(), !isActive, isActive);
    }

    public void findTrainersTrainingListByCriteria(String username, LocalDate fromDate, LocalDate toDate, String traineeFirstName, String trainingType) {
        log.info("Received request for getting trainerTrainings by criteria, {}, {}, {}, {}, {}", username, fromDate, toDate, traineeFirstName, trainingType);
        List<Training> trainerTrainings = trainingService.findTrainerTrainings(username, fromDate, toDate, traineeFirstName, trainingType);
        log.info("List of trainings found: {}", trainerTrainings);
    }

    public Trainer findTrainerById(Long id) {
        log.info("Fetching Trainer by ID: {}", id);
        Trainer trainer = trainerService.findById(id);
        if (trainer != null) {
            log.info("Trainer found: {}", trainer.getUsername());
        } else {
            log.warn("Trainer not found with ID: {}", id);
        }
        return trainer;
    }

    public Trainer updateTrainer(Trainer trainer) {
        log.info("Updating Trainer with ID: {}", trainer.getId());
        Trainer updatedTrainer = trainerService.update(trainer);
        log.info("Trainer updated: {}", updatedTrainer.getUsername());
        return updatedTrainer;
    }

    // ------------- TRAINING OPERATIONS -----------------
    public List<Training> findAllTrainings() {
        log.info("Fetching all trainings.");
        List<Training> trainings = trainingService.findAll();
        log.info("Found {} trainings.", trainings.size());
        return trainings;
    }

    public Training findTrainingById(Long id) {
        log.info("Fetching Training by ID: {}", id);
        Training training = trainingService.findById(id);
        if (training != null) {
            log.info("Training found: {}", training.getName());
        } else {
            log.warn("Training not found with ID: {}", id);
        }
        return training;
    }

    public Training createTraining(Training training) {
        log.info("Creating Training: {}", training.getName());
        Training createdTraining = trainingService.create(training);
        log.info("Training created: {}", createdTraining);
        return createdTraining;
    }
}
