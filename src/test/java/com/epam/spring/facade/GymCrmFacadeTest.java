package com.epam.spring.facade;

import com.epam.spring.config.AppConfig;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import com.epam.spring.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(AppConfig.class)
@TestPropertySource(locations = "classpath:test-application.properties")
class GymCrmFacadeTest {

    @Autowired
    private InMemoryStorage inMemoryStorage;
    @Autowired
    private GymCrmFacade facade;

    @BeforeEach
    public void clearDB() {
        inMemoryStorage.clearDB();
    }

    @Test
    void whenCreateTrainee_ThenShouldBeSaved_AndShouldHaveCorrectUsernameAndPassword() {
        // given
        Trainee trainee = buildTrainee("Will", "Salas");

        // when
        Trainee createdTrainee = facade.createTrainee(trainee);
        Trainee savedTrainee = facade.findTraineeById(createdTrainee.getUserId());

        // then
        assertNotNull(createdTrainee.getUserId());
        assertSame(createdTrainee, savedTrainee);
        assertEquals("Will.Salas", createdTrainee.getUsername());
        assertEquals(10, createdTrainee.getPassword().length());
    }

    @Test
    void whenCreateTraineeWithExistingNamesTwice_ThenSerialShouldBeAddedToUsername() {
        // given
        Trainee trainee1 = buildTrainee("Will", "Salas");
        Trainee trainee2 = buildTrainee("Will", "Salas");

        // when
        Trainee createdTrainee1 = facade.createTrainee(trainee1);
        Trainee createdTrainee2 = facade.createTrainee(trainee2);

        // then
        assertEquals("Will.Salas", createdTrainee1.getUsername());
        assertEquals("Will.Salas.1", createdTrainee2.getUsername());
    }

    @Test
    void testFindAllTrainees() {
        Trainee trainee1 = facade.createTrainee(buildTrainee("Will", "Salas"));
        Trainee trainee2 = facade.createTrainee(buildTrainee("Adam", "Simpson"));

        List<Trainee> trainees = facade.findAllTrainees();

        assertEquals(2, trainees.size());
        assertTrue(trainees.contains(trainee1));
        assertTrue(trainees.contains(trainee2));
    }

    @Test
    void testFindTraineeById() {
        Trainee trainee = facade.createTrainee(buildTrainee("Will", "Salas"));

        Trainee traineeById = facade.findTraineeById(trainee.getUserId());

        assertSame(trainee, traineeById);
    }

    @Test
    void testUpdateTrainee() {
        Trainee createdTrainee = facade.createTrainee(buildTrainee("Will", "Salas"));

        String expectedFirstname = "Adam";
        String expectedLastName = "Simpson";
        String expectedUsername = expectedFirstname + "." + expectedLastName;

        createdTrainee.setFirstName(expectedFirstname);
        createdTrainee.setLastName(expectedLastName);
        Trainee updatedTrainee = facade.updateTrainee(createdTrainee);

        assertSame(updatedTrainee.getUserId(), createdTrainee.getUserId());
        assertEquals(expectedFirstname, updatedTrainee.getFirstName());
        assertEquals(expectedLastName, updatedTrainee.getLastName());
        assertEquals(expectedUsername, updatedTrainee.getUsername());
    }

    @Test
    void testDeleteTrainee() {
        Trainee createdTrainee = facade.createTrainee(buildTrainee("Will", "Salas"));

        facade.deleteTrainee(createdTrainee);

        assertEquals(0, facade.findAllTrainees().size());
        assertNull(facade.findTraineeById(createdTrainee.getUserId()));
    }

    @Test
    void testCreateTrainer() {
        // given
        Trainer trainee = buildTrainer("Will", "Salas");

        // when
        Trainer createdTrainer = facade.createTrainer(trainee);
        Trainer savedTrainer = facade.findTrainerById(createdTrainer.getUserId());

        // then
        assertNotNull(createdTrainer.getUserId());
        assertSame(createdTrainer, savedTrainer);
        assertEquals("Will.Salas", createdTrainer.getUsername());
        assertEquals(10, createdTrainer.getPassword().length());
    }

    @Test
    void testFindAllTrainers() {
        Trainer trainer1 = facade.createTrainer(buildTrainer("Will", "Salas"));
        Trainer trainer2 = facade.createTrainer(buildTrainer("Adam", "Simpson"));

        List<Trainer> trainers = facade.findAllTrainers();

        assertEquals(2, trainers.size());
        assertTrue(trainers.contains(trainer1));
        assertTrue(trainers.contains(trainer2));
    }

    @Test
    void testUpdateTrainer() {
        Trainer createdTrainer = facade.createTrainer(buildTrainer("Will", "Salas"));

        String expectedFirstname = "Adam";
        String expectedLastName = "Simpson";
        String expectedUsername = expectedFirstname + "." + expectedLastName;

        createdTrainer.setFirstName(expectedFirstname);
        createdTrainer.setLastName(expectedLastName);
        Trainer updatedTrainer = facade.updateTrainer(createdTrainer);

        assertSame(updatedTrainer.getUserId(), createdTrainer.getUserId());
        assertEquals(expectedFirstname, updatedTrainer.getFirstName());
        assertEquals(expectedLastName, updatedTrainer.getLastName());
        assertEquals(expectedUsername, updatedTrainer.getUsername());
    }

    @Test
    void testCreateTraining() {
        Trainee trainee = facade.createTrainee(buildTrainee("Adam", "Simpson"));
        Trainer trainer = facade.createTrainer(buildTrainer("Will", "Salas"));

        Training training = new Training(trainee, trainer, "Hard Cardio", TrainingType.CARDIO, LocalDateTime.now().plusHours(5), 90);

        Training createdTraining = facade.createTraining(training);

        assertNotNull(createdTraining.getUuid());
        assertEquals(createdTraining, facade.findTrainingById(createdTraining.getUuid()));
        assertEquals(1, facade.findAllTrainings().size());
    }

    private static Trainee buildTrainee(String firstName, String lastName) {
        return new Trainee(firstName, lastName, LocalDate.now().minusYears(15), "Nukus", true);
    }

    private Trainer buildTrainer(String firstName, String lastName) {
        return new Trainer(firstName, lastName, "Strength Training", true);
    }
}