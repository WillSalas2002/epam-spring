package com.epam.spring.storage;

import com.epam.spring.config.AppConfig;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringJUnitConfig(AppConfig.class)
@TestPropertySource(locations = "classpath:test-application.properties")
class InMemoryStorageTest {

    @Autowired
    private InMemoryStorage inMemoryStorage;

    @Value("${storage.trainee.initial-data-path}")
    private String filePath;

    @BeforeEach
    void setupTestProperties() {
        inMemoryStorage.clearDB();
    }

    @Test
    public void testInitialDataLoadingLogic() {
        inMemoryStorage.loadInitialData(filePath);

        List<Trainee> trainees = inMemoryStorage.findAllTrainees();
        List<Trainer> trainers = inMemoryStorage.findAllTrainers();
        List<Training> trainings = inMemoryStorage.findAllTrainings();

        assertEquals(1, trainees.size());
        assertEquals(1, trainers.size());
        assertEquals(1, trainings.size());
    }

    @Test
    public void testCreateTrainee() {
        // given
        Trainee trainee = buildTrainee();

        // when
        Trainee createdTrainee = inMemoryStorage.createTrainee(trainee);

        // then
        assertNotNull(createdTrainee.getUserId());
        assertEquals(1, inMemoryStorage.findAllTrainees().size());
        assertEquals(createdTrainee, inMemoryStorage.findTraineeById(createdTrainee.getUserId()));
    }

    @Test
    public void testUpdateTrainee() {
        // given
        Trainee trainee = buildTrainee();
        Trainee createdTrainee = inMemoryStorage.createTrainee(trainee);

        // when
        createdTrainee.setFirstName("Jane");
        createdTrainee.setAddress("456 Updated St");
        Trainee updatedTrainee = inMemoryStorage.updateTrainee(createdTrainee);

        // then
        assertEquals("Jane", updatedTrainee.getFirstName());
        assertEquals("456 Updated St", updatedTrainee.getAddress());
        assertEquals(1, inMemoryStorage.findAllTrainees().size());
    }

    @Test
    public void testDeleteTrainee() {
        // given
        Trainee trainee = buildTrainee();
        Trainee createdTrainee = inMemoryStorage.createTrainee(trainee);

        // when
        inMemoryStorage.deleteTrainee(createdTrainee);

        // then
        assertEquals(0, inMemoryStorage.findAllTrainees().size());
        assertNull(inMemoryStorage.findTraineeById(createdTrainee.getUserId()));
    }

    @Test
    public void testCreateTrainer() {
        // given
        Trainer trainer = buildTrainer();

        // when
        Trainer createdTrainer = inMemoryStorage.createTrainer(trainer);

        // then
        assertNotNull(createdTrainer.getUserId());
        assertEquals(1, inMemoryStorage.findAllTrainers().size());
        assertEquals(createdTrainer, inMemoryStorage.findTrainerById(createdTrainer.getUserId()));
    }

    @Test
    public void testUpdateTrainer() {
        // given
        Trainer trainer = buildTrainer();
        Trainer createdTrainer = inMemoryStorage.createTrainer(trainer);

        // when
        String expectedFirstName = "Will";
        createdTrainer.setFirstName(expectedFirstName);
        createdTrainer.setActive(false);
        Trainer updatedTrainer = inMemoryStorage.updateTrainer(createdTrainer);

        // then
        assertNotNull(updatedTrainer.getUserId());
        assertEquals(expectedFirstName, updatedTrainer.getFirstName());
        assertFalse(updatedTrainer.isActive());
    }

    @Test
    public void testDeleteTrainer() {
        // given
        Trainer trainer = buildTrainer();
        Trainer createdTrainer = inMemoryStorage.createTrainer(trainer);

        // when
        inMemoryStorage.deleteTrainer(createdTrainer);

        // then
        assertEquals(0, inMemoryStorage.findAllTrainers().size());
        assertNull(inMemoryStorage.findTraineeById(createdTrainer.getUserId()));
    }

    @Test
    public void testCreateTraining() {
        // given
        Trainee trainee = buildTrainee();
        Trainee createdTrainee = inMemoryStorage.createTrainee(trainee);

        Trainer trainer = buildTrainer();
        Trainer createdTrainer = inMemoryStorage.createTrainer(trainer);

        Training training = new Training();
        training.setTrainee(createdTrainee);
        training.setTrainer(createdTrainer);
        training.setName("Cardio Training");
        training.setDate(LocalDateTime.now());
        training.setDuration(60);

        // when
        Training createdTraining = inMemoryStorage.createTraining(training);

        // then
        assertNotNull(createdTraining.getUuid());
        assertEquals(1, inMemoryStorage.findAllTrainings().size());
        assertEquals(createdTraining, inMemoryStorage.findTrainingById(createdTraining.getUuid()));
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