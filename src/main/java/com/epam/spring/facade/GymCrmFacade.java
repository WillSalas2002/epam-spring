package com.epam.spring.facade;

import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.service.TraineeService;
import com.epam.spring.service.TrainerService;
import com.epam.spring.service.TrainingService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Component
public class GymCrmFacade {

    private static final Logger LOGGER = Logger.getLogger(GymCrmFacade.class.getName());

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymCrmFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public List<Trainee> findAllTrainees() {
        LOGGER.info("Fetching all trainees.");
        List<Trainee> trainees = traineeService.findAll();
        LOGGER.info("Found " + trainees.size() + " trainees.");
        return trainees;
    }

    public Trainee createTrainee(Trainee trainee) {
        LOGGER.info("Creating Trainee with name: " + trainee.getFirstName());
        Trainee createdTrainee = traineeService.create(trainee);
        LOGGER.info("Trainee created: " + createdTrainee);
        return createdTrainee;
    }

    public Trainee findTraineeById(UUID uuid) {
        LOGGER.info("Fetching Trainee by ID: " + uuid);
        Trainee trainee = traineeService.findById(uuid);
        if (trainee != null) {
            LOGGER.info("Trainee found: " + trainee.getUsername());
        } else {
            LOGGER.warning("Trainee not found with ID: " + uuid);
        }
        return trainee;
    }

    public Trainee updateTrainee(Trainee trainee) {
        LOGGER.info("Updating Trainee with ID: " + trainee.getUserId());
        Trainee updatedTrainee = traineeService.update(trainee);
        LOGGER.info("Trainee updated: " + updatedTrainee.getUsername());
        return updatedTrainee;
    }

    public void deleteTrainee(Trainee trainee) {
        LOGGER.info("Deleting Trainee with ID: " + trainee.getUserId());
        traineeService.delete(trainee);
        LOGGER.info("Trainee deleted: " + trainee.getUsername());
    }

    public List<Trainer> findAllTrainers() {
        LOGGER.info("Fetching all trainers.");
        List<Trainer> trainers = trainerService.findAll();
        LOGGER.info("Found " + trainers.size() + " trainers.");
        return trainers;
    }

    public Trainer createTrainer(Trainer trainer) {
        LOGGER.info("Creating Trainer with name: " + trainer.getFirstName());
        Trainer createdTrainer = trainerService.create(trainer);
        LOGGER.info("Trainer created: " + createdTrainer);
        return createdTrainer;
    }

    public Trainer findTrainerById(UUID uuid) {
        LOGGER.info("Fetching Trainer by ID: " + uuid);
        Trainer trainer = trainerService.findById(uuid);
        if (trainer != null) {
            LOGGER.info("Trainer found: " + trainer.getUsername());
        } else {
            LOGGER.warning("Trainer not found with ID: " + uuid);
        }
        return trainer;
    }

    public Trainer updateTrainer(Trainer trainer) {
        LOGGER.info("Updating Trainer with ID: " + trainer.getUserId());
        Trainer updatedTrainer = trainerService.update(trainer);
        LOGGER.info("Trainer updated: " + updatedTrainer.getUsername());
        return updatedTrainer;
    }

    public List<Training> findAllTrainings() {
        LOGGER.info("Fetching all trainings.");
        List<Training> trainings = trainingService.findAll();
        LOGGER.info("Found " + trainings.size() + " trainings.");
        return trainings;
    }

    public Training findTrainingById(UUID uuid) {
        LOGGER.info("Fetching Training by ID: " + uuid);
        Training training = trainingService.findById(uuid);
        if (training != null) {
            LOGGER.info("Training found: " + training.getName());
        } else {
            LOGGER.warning("Training not found with ID: " + uuid);
        }
        return training;
    }

    public Training createTraining(Training training) {
        LOGGER.info("Creating Training: " + training.getName());
        Training createdTraining = trainingService.create(training);
        LOGGER.info("Training created: " + createdTraining);
        return createdTraining;
    }
}
