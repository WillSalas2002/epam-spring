package com.epam.spring.repository;

import com.epam.spring.config.AppConfig;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import com.epam.spring.service.TraineeService;
import com.epam.spring.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(AppConfig.class)
class TrainingRepositoryTest {

    @Autowired
    private TrainingRepository trainingRepository;
    @Autowired
    private TraineeService traineeService;
    @Autowired
    private TrainerService trainerService;

    private Trainer trainer;
    private Trainee trainee;

    @BeforeEach
    void clearStorage() {
        trainer = buildTrainer("Will", "Salas");
        trainee = buildTrainee("Adam", "Simpson");
    }

    @Test
    public void testCreateTraining() {
        Trainee createdTrainee = traineeService.create(trainee);
        Trainer createdTrainer = trainerService.create(trainer);
        Training training = new Training(createdTrainee, createdTrainer, "Strong man training", new TrainingType(), LocalDateTime.now().plusHours(3), 120);

        Training createdTraining = trainingRepository.create(training);
        Training trainingById = trainingRepository.findById(createdTraining.getId());

        assertNotNull(createdTraining);
        assertNotNull(trainingById);
        assertEquals(120, trainingById.getDuration());
        assertEquals(1, trainingRepository.findAll().size());

    }

    private Trainer buildTrainer(String firstName, String lastName) {
        return Trainer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    private Trainee buildTrainee(String firstName, String lastName) {
        return Trainee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dataOfBirth(LocalDate.now().minusYears(25))
                .address("Test Address")
                .isActive(true)
                .build();
    }
}