package com.epam.spring.storage;

import com.epam.spring.config.AppConfig;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.utils.StorageClearer;
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

    @Autowired
    private StorageClearer storageClearer;

    @Value("${storage.trainee.initial-data-path}")
    private String filePath;

    @BeforeEach
    void setUp() {
        storageClearer.clear();
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
        Trainee trainee = buildTrainee();

        Trainee createdTrainee = inMemoryStorage.createTrainee(trainee);

        assertNotNull(createdTrainee.getUuid());
        assertEquals(1, inMemoryStorage.findAllTrainees().size());
        assertEquals(createdTrainee, inMemoryStorage.findTraineeById(createdTrainee.getUuid()));
    }

    @Test
    public void testUpdateTrainee() {
        Trainee trainee = buildTrainee();
        Trainee createdTrainee = inMemoryStorage.createTrainee(trainee);

        createdTrainee.setFirstName("Jane");
        createdTrainee.setAddress("456 Updated St");
        Trainee updatedTrainee = inMemoryStorage.updateTrainee(createdTrainee);

        assertEquals("Jane", updatedTrainee.getFirstName());
        assertEquals("456 Updated St", updatedTrainee.getAddress());
        assertEquals(1, inMemoryStorage.findAllTrainees().size());
    }

    @Test
    public void testDeleteTrainee() {
        Trainee trainee = buildTrainee();
        Trainee createdTrainee = inMemoryStorage.createTrainee(trainee);

        inMemoryStorage.deleteTrainee(createdTrainee);

        assertEquals(0, inMemoryStorage.findAllTrainees().size());
        assertNull(inMemoryStorage.findTraineeById(createdTrainee.getUuid()));
    }

    @Test
    public void testCreateTrainer() {
        Trainer trainer = buildTrainer();

        Trainer createdTrainer = inMemoryStorage.createTrainer(trainer);

        assertNotNull(createdTrainer.getUuid());
        assertEquals(1, inMemoryStorage.findAllTrainers().size());
        assertEquals(createdTrainer, inMemoryStorage.findTrainerById(createdTrainer.getUuid()));
    }

    @Test
    public void testUpdateTrainer() {
        Trainer trainer = buildTrainer();
        Trainer createdTrainer = inMemoryStorage.createTrainer(trainer);

        String expectedFirstName = "Will";
        createdTrainer.setFirstName(expectedFirstName);
        createdTrainer.setActive(false);
        Trainer updatedTrainer = inMemoryStorage.updateTrainer(createdTrainer);

        assertNotNull(updatedTrainer.getUuid());
        assertEquals(expectedFirstName, updatedTrainer.getFirstName());
        assertFalse(updatedTrainer.isActive());
    }

    @Test
    public void testDeleteTrainer() {
        Trainer trainer = buildTrainer();
        Trainer createdTrainer = inMemoryStorage.createTrainer(trainer);

        inMemoryStorage.deleteTrainer(createdTrainer);

        assertEquals(0, inMemoryStorage.findAllTrainers().size());
        assertNull(inMemoryStorage.findTraineeById(createdTrainer.getUuid()));
    }

    @Test
    public void testCreateTraining() {
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

        Training createdTraining = inMemoryStorage.createTraining(training);

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