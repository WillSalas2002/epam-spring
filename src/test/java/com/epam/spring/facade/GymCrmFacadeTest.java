package com.epam.spring.facade;

import com.epam.spring.config.AppConfig;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import com.epam.spring.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(AppConfig.class)
@TestPropertySource(locations = "classpath:test-application.properties")
class GymCrmFacadeTest {

    @Autowired
    private GymCrmFacade facade;

    @Autowired
    private SessionFactory sessionFactory;

    private Trainee admin;

    @BeforeEach
    void setUp() {
        admin = facade.createTrainee(buildTrainee("Admin", "Admin"));
    }

    @AfterEach
    void tearDown() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createMutationQuery("DELETE FROM Training").executeUpdate();
            session.createMutationQuery("DELETE FROM Trainee").executeUpdate();
            session.createMutationQuery("DELETE FROM Trainer").executeUpdate();
            session.createMutationQuery("DELETE FROM TrainingType").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    void whenCreateTrainee_ThenShouldBeSaved_AndShouldHaveCorrectUsernameAndPassword() {
        Trainee trainee = buildTrainee("Will", "Salas");

        Trainee createdTrainee = facade.createTrainee(trainee);
        Trainee savedTrainee = facade.findTraineeById(createdTrainee.getId(), admin.getUser().getUsername(), admin.getUser().getPassword());

        assertNotNull(createdTrainee.getId());
        assertEquals(createdTrainee, savedTrainee);
        assertEquals("Will.Salas", createdTrainee.getUser().getUsername());
        assertEquals(10, createdTrainee.getUser().getPassword().length());
    }

    @Test
    void whenCreateTraineeWithExistingNamesTwice_ThenSerialShouldBeAddedToUsername() {
        Trainee trainee1 = buildTrainee("Will", "Salas");
        Trainee trainee2 = buildTrainee("Will", "Salas");

        Trainee createdTrainee1 = facade.createTrainee(trainee1);
        Trainee createdTrainee2 = facade.createTrainee(trainee2);

        assertEquals("Will.Salas", createdTrainee1.getUser().getUsername());
        assertEquals("Will.Salas.1", createdTrainee2.getUser().getUsername());
    }

    @Test
    void testFindAllTrainees() {
        Trainee trainee1 = facade.createTrainee(buildTrainee("Will", "Salas"));
        Trainee trainee2 = facade.createTrainee(buildTrainee("Adam", "Simpson"));

        List<Trainee> trainees = facade.findAllTrainees(admin.getUser().getUsername(), admin.getUser().getPassword());

        assertEquals(3, trainees.size());
        assertTrue(trainees.contains(trainee1));
        assertTrue(trainees.contains(trainee2));
    }

    @Test
    void testFindTraineeById() {
        Trainee trainee = facade.createTrainee(buildTrainee("Will", "Salas"));

        Trainee traineeById = facade.findTraineeById(trainee.getId(), admin.getUser().getUsername(), admin.getUser().getPassword());

        assertEquals(trainee, traineeById);
    }

    @Test
    void testUpdateTrainee() {
        Trainee createdTrainee = facade.createTrainee(buildTrainee("Will", "Salas"));

        String expectedFirstname = "Adam";
        String expectedLastName = "Simpson";
        String expectedUsername = expectedFirstname + "." + expectedLastName;

        createdTrainee.getUser().setFirstName(expectedFirstname);
        createdTrainee.getUser().setLastName(expectedLastName);
        Trainee updatedTrainee = facade.updateTrainee(createdTrainee, admin.getUser().getUsername(), admin.getUser().getPassword());

        assertSame(updatedTrainee.getId(), createdTrainee.getId());
        assertEquals(expectedFirstname, updatedTrainee.getUser().getFirstName());
        assertEquals(expectedLastName, updatedTrainee.getUser().getLastName());
        assertEquals(expectedUsername, updatedTrainee.getUser().getUsername());
    }

    @Test
    void testDeleteTrainee() {
        Trainee createdTrainee = facade.createTrainee(buildTrainee("Will", "Salas"));
        long id = createdTrainee.getId();

        facade.deleteTrainee(createdTrainee, admin.getUser().getUsername(), admin.getUser().getPassword());

        assertEquals(1, facade.findAllTrainees(admin.getUser().getUsername(), admin.getUser().getPassword()).size());
        assertThrows(RuntimeException.class, () -> facade.findTraineeById(id, admin.getUser().getUsername(), admin.getUser().getPassword()), "Trainee with id " + id + " not found");

    }

    @Test
    void testCreateTrainer() {
        Trainer trainee = buildTrainer("Will", "Salas");

        Trainer createdTrainer = facade.createTrainer(trainee);
        Trainer savedTrainer = facade.findTrainerById(createdTrainer.getId(), admin.getUser().getUsername(), admin.getUser().getPassword());

        assertNotNull(createdTrainer.getId());
        assertEquals(createdTrainer, savedTrainer);
        assertEquals("Will.Salas", createdTrainer.getUser().getUsername());
        assertEquals(10, createdTrainer.getUser().getPassword().length());
    }

    @Test
    void testFindAllTrainers() {
        Trainer trainer1 = facade.createTrainer(buildTrainer("Will", "Salas"));
        Trainer trainer2 = facade.createTrainer(buildTrainer("Adam", "Simpson"));

        List<Trainer> trainers = facade.findAllTrainers(admin.getUser().getUsername(), admin.getUser().getPassword());

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

        createdTrainer.getUser().setFirstName(expectedFirstname);
        createdTrainer.getUser().setLastName(expectedLastName);
        Trainer updatedTrainer = facade.updateTrainer(createdTrainer, admin.getUser().getUsername(), admin.getUser().getPassword());

        assertSame(updatedTrainer.getId(), createdTrainer.getId());
        assertEquals(expectedFirstname, updatedTrainer.getUser().getFirstName());
        assertEquals(expectedLastName, updatedTrainer.getUser().getLastName());
        assertEquals(expectedUsername, updatedTrainer.getUser().getUsername());
    }

    @Test
    void testCreateTraining() {
        Trainee trainee = facade.createTrainee(buildTrainee("Adam", "Simpson"));
        Trainer trainer = facade.createTrainer(buildTrainer("Will", "Salas"));
        TrainingType trainingType = trainer.getSpecialization();
        Training training = buildTraining(trainee, trainer, trainingType);

        Training createdTraining = facade.createTraining(training, admin.getUser().getUsername(), admin.getUser().getPassword());

        assertNotNull(createdTraining.getId());
        assertEquals(1, facade.findAllTrainings(admin.getUser().getUsername(), admin.getUser().getPassword()).size());
    }

    private static Training buildTraining(Trainee trainee, Trainer trainer, TrainingType trainingType) {
        return Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .name("Hard Cardio")
                .trainingType(trainingType)
                .date(LocalDateTime.now().plusHours(5))
                .duration(90)
                .build();
    }

    private Trainer buildTrainer(String firstName, String lastName) {
        return Trainer.builder()
                .user(User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .isActive(true)
                        .build()
                )
                .specialization(buildTrainingType())
                .build();
    }

    private static Trainee buildTrainee(String firstName, String lastName) {
        return Trainee.builder()
                .user(User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .isActive(true)
                        .build()
                )
                .dataOfBirth(LocalDate.now().minusYears(25))
                .address("Test Address")
                .build();
    }

    private static TrainingType buildTrainingType() {
        return TrainingType.builder()
                .trainingTypeName("Cardio")
                .build();
    }
}