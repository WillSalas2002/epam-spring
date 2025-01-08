package com.epam.spring.service;

import com.epam.spring.config.AppConfig;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.TrainingType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(AppConfig.class)
class TrainerServiceTest {

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private SessionFactory sessionFactory;

    private Trainer trainer1;
    private Trainer trainer2;

    @BeforeEach
    void setUp() {
        trainer1 = buildTrainer("John", "Doe");
        trainer2 = buildTrainer("Will", "Salas");
    }

    @AfterEach
    void tearDown() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createMutationQuery("DELETE FROM Trainer").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    void testCreate() {
        Trainer createdTrainer = trainerService.create(trainer1);

        assertNotNull(createdTrainer.getId());
        assertEquals("John.Doe", createdTrainer.getUsername());
        assertEquals(1, trainerService.findAll().size());
    }

    @Test
    void testCreateWithExistingName() {
        Trainer createdTrainer1 = trainerService.create(trainer1);
        trainer2.setFirstName("John");
        trainer2.setLastName("Doe");
        Trainer createdTrainer2 = trainerService.create(trainer2);

        assertEquals("John.Doe", createdTrainer1.getUsername());
        assertEquals("John.Doe.1", createdTrainer2.getUsername());
    }

    @Test
    void testFindAll() {
        Trainer createdTrainer1 = trainerService.create(trainer1);
        Trainer createdTrainer2 = trainerService.create(trainer2);

        List<Trainer> Trainers = trainerService.findAll();

        assertEquals(2, Trainers.size());
        assertTrue(Trainers.stream().anyMatch(t -> t.getUsername().equals(createdTrainer1.getUsername())));
        assertTrue(Trainers.stream().anyMatch(t -> t.getUsername().equals(createdTrainer2.getUsername())));
    }

    @Test
    void testFindById() {
        Trainer createdTrainer = trainerService.create(trainer1);

        Trainer foundTrainer = trainerService.findById(createdTrainer.getId());

        assertNotNull(foundTrainer);
        assertEquals(createdTrainer.getId(), foundTrainer.getId());
        assertEquals(trainer1.getUsername(), foundTrainer.getUsername());
    }

    @Test
    void testFindByIdNonExistent() {
        long nonExistentId = 100L;
        assertThrows(RuntimeException.class, () -> trainerService.findById(nonExistentId), "Trainer with id " + nonExistentId + " not found");
    }

    @Test
    void testUpdate() {
        Trainer createdTrainer = trainerService.create(trainer1);

        createdTrainer.setFirstName("Updated");
        createdTrainer.setLastName("Name");
        Trainer updatedTrainer = trainerService.update(createdTrainer);

        assertEquals("Updated", updatedTrainer.getFirstName());
        assertEquals("Name", updatedTrainer.getLastName());
        assertEquals("Updated.Name", updatedTrainer.getUsername());
        assertEquals(createdTrainer.getId(), updatedTrainer.getId());
        assertEquals(1, trainerService.findAll().size());
    }

    @Test
    void whenUpdateNonExistingTrainerThenThrowException() {
        long id = 10L;
        trainer1.setId(id);
        assertThrows(NoSuchElementException.class, () -> trainerService.update(trainer1), "Trainer with id " + id + " not found");
    }

    @Test
    void testDelete() {
        Trainer createdTrainer = trainerService.create(trainer1);
        Long id = createdTrainer.getId();

        trainerService.delete(createdTrainer);

        assertEquals(0, trainerService.findAll().size());
        assertThrows(RuntimeException.class, () -> trainerService.findById(createdTrainer.getId()), "Trainer with id " + id + " not found");
    }

    @Test
    void testDeleteNonExistentTrainer() {
        assertDoesNotThrow(() -> trainerService.delete(trainer1));
    }

    @Test
    void testFindByUsername() {
        Trainer createdTrainer = trainerService.create(trainer1);

        Trainer Trainer = trainerService.findByUsername(trainer1.getUsername());

        assertEquals(createdTrainer.getId(), Trainer.getId());
    }

    @Test
    void testActivateShouldNotBeIdempotent() {
        Trainer createdTrainer = trainerService.create(trainer1);

        trainerService.activate(createdTrainer);

        assertFalse(createdTrainer.isActive());

        trainerService.activate(createdTrainer);

        assertTrue(createdTrainer.isActive());
    }

    @Test
    void testChangePassword() {
        String newPassword = "1111111111";
        Trainer trainer = trainerService.create(trainer1);
        String username = trainer.getUsername();

        trainerService.changePassword(username, trainer.getPassword(), newPassword);

        assertEquals(newPassword, trainerService.findByUsername(username).getPassword());
    }

    @Test
    void whenChangePasswordWithIncorrectOldPasswordThenThrowException() {
        String incorrectOldPassword = "1111111111";
        String newPassword = "1132211111";
        Trainer Trainer = trainerService.create(trainer1);
        String username = Trainer.getUsername();

        assertThrows(RuntimeException.class, () -> trainerService.changePassword(username, incorrectOldPassword, newPassword), "Incorrect password");
    }

    private Trainer buildTrainer(String firstName, String lastName) {
        return Trainer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .specialization(buildTrainingType())
                .isActive(true)
                .build();
    }

    private static TrainingType buildTrainingType() {
        return TrainingType.builder()
                .trainingTypeName("Cardio")
                .build();
    }
}