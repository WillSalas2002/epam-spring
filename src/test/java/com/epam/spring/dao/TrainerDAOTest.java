package com.epam.spring.dao;

import com.epam.spring.config.AppConfig;
import com.epam.spring.model.Trainer;
import com.epam.spring.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(AppConfig.class)
class TrainerDAOTest {

    @Autowired
    private TrainerDAO trainerDAO;

    @Autowired
    private InMemoryStorage inMemoryStorage;

    @BeforeEach
    void setUp() {
        inMemoryStorage.clearDB();
    }

    @Test
    void testCreate() {
        // Given
        Trainer trainer = buildTrainer();

        // When
        Trainer createdTrainer = trainerDAO.create(trainer);

        // Then
        assertNotNull(createdTrainer.getUserId());
        assertTrue(inMemoryStorage.findAllTrainers().contains(createdTrainer));
    }

    @Test
    void testFindAll() {
        // Given
        Trainer trainer1 = buildTrainer();
        Trainer trainer2 = buildTrainer();
        trainer2.setFirstName("Jane");

        // When
        trainerDAO.create(trainer1);
        trainerDAO.create(trainer2);
        List<Trainer> trainers = trainerDAO.findAll();

        // Then
        assertEquals(2, trainers.size());
        assertTrue(trainers.stream().anyMatch(t -> t.getFirstName().equals("John")));
        assertTrue(trainers.stream().anyMatch(t -> t.getFirstName().equals("Jane")));
    }

    @Test
    void testFindById() {
        // Given
        Trainer trainer = buildTrainer();
        Trainer createdTrainer = trainerDAO.create(trainer);

        // When
        Trainer foundTrainer = trainerDAO.findById(createdTrainer.getUserId());

        // Then
        assertNotNull(foundTrainer);
        assertEquals(createdTrainer.getUserId(), foundTrainer.getUserId());
    }

    @Test
    void testFindByIdNonExistent() {
        // When
        Trainer foundTrainer = trainerDAO.findById(UUID.randomUUID());

        // Then
        assertNull(foundTrainer);
    }

    @Test
    void testUpdate() {
        // Given
        Trainer trainer = buildTrainer();
        Trainer createdTrainer = trainerDAO.create(trainer);

        // When
        createdTrainer.setFirstName("Updated");
        createdTrainer.setLastName("Name");
        Trainer updatedTrainer = trainerDAO.update(createdTrainer);

        // Then
        assertEquals("Updated", updatedTrainer.getFirstName());
        assertEquals("Name", updatedTrainer.getLastName());
        assertEquals(createdTrainer.getUserId(), updatedTrainer.getUserId());
    }

    @Test
    void testDelete() {
        // Given
        Trainer trainer = buildTrainer();
        Trainer createdTrainer = trainerDAO.create(trainer);

        // When
        trainerDAO.delete(createdTrainer);

        // Then
        assertEquals(0, trainerDAO.findAll().size());
        assertNull(trainerDAO.findById(createdTrainer.getUserId()));
    }

    @Test
    void testDeleteNonExistentTrainee() {
        // Given
        Trainer trainer = buildTrainer();

        // When & Then
        assertDoesNotThrow(() -> trainerDAO.delete(trainer));
    }

    @Test
    void testMultipleOperations() {
        // Create
        Trainer trainer1 = buildTrainer();
        Trainer createdTrainer1 = trainerDAO.create(trainer1);

        // Update
        createdTrainer1.setSpecialization("Cardio");
        Trainer updatedTrainer1 = trainerDAO.update(createdTrainer1);

        // Create another
        Trainer trainer2 = buildTrainer();
        trainer2.setFirstName("Jane");
        Trainer createdTrainer2 = trainerDAO.create(trainer2);

        // Find
        List<Trainer> trainers = trainerDAO.findAll();
        Trainer foundTrainer = trainerDAO.findById(createdTrainer1.getUserId());

        // Delete
        trainerDAO.delete(createdTrainer1);

        // Assertions
        assertEquals(2, trainers.size());
        assertEquals("Cardio", updatedTrainer1.getSpecialization());
        assertEquals("Cardio", foundTrainer.getSpecialization());
        assertEquals(1, trainerDAO.findAll().size());
    }

    private Trainer buildTrainer() {
        return new Trainer("John", "Doe", "Strength Training", true);
    }
}