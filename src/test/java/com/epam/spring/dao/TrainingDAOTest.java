package com.epam.spring.dao;

import com.epam.spring.config.AppConfig;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import com.epam.spring.utils.StorageClearer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(AppConfig.class)
class TrainingDAOTest {

    @Autowired
    private TrainingDAO trainingDAO;

    @Autowired
    private StorageClearer storageClearer;

    @BeforeEach
    void clearStorage() {
        storageClearer.clear();
    }

    @Test
    public void testCreateTraining() {
        Trainer trainer = buildTrainer();
        Trainee trainee = buildTrainee();
        Training training = new Training(trainee, trainer, "Strong man training", TrainingType.STRENGTH_TRAINING, LocalDateTime.now().plusHours(3), 120);

        Training createdTraining = trainingDAO.create(training);

        assertNotNull(createdTraining);
        assertEquals(createdTraining, trainingDAO.findById(createdTraining.getUuid()));
        assertEquals(1, trainingDAO.findAll().size());
        assertTrue(trainingDAO.findAll().contains(createdTraining));

    }

    private static Trainer buildTrainer() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");
        trainer.setUsername("jane.smith");
        trainer.setPassword("password456");
        trainer.setSpecialization("Fitness");
        trainer.setActive(true);
        return trainer;
    }

    private static Trainee buildTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setUsername("john.doe");
        trainee.setPassword("password123");
        trainee.setDataOfBirth(LocalDate.now());
        trainee.setAddress("123 Test St");
        trainee.setActive(true);
        return trainee;
    }
}