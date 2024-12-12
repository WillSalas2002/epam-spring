package com.epam.spring.service;

import com.epam.spring.config.AppConfig;
import com.epam.spring.model.Trainer;
import com.epam.spring.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(AppConfig.class)
class TrainerServiceTest {

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private InMemoryStorage inMemoryStorage;

    @BeforeEach
    void setUp() {
        inMemoryStorage.clearDB();
    }

    @Test
    void testCreate() {
        // Given
        Trainer Trainer = buildTrainer();

        // When
        Trainer createdTrainer = trainerService.create(Trainer);

        // Then
        assertNotNull(createdTrainer.getUserId());
        assertEquals("John.Doe", createdTrainer.getUsername());
        assertTrue(inMemoryStorage.findAllTrainers().contains(createdTrainer));
    }

    @Test
    void testCreateWithExistingName() {
        // Given
        Trainer trainer1 = buildTrainer();
        Trainer trainer2 = buildTrainer();

        // When
        Trainer createdTrainer1 = trainerService.create(trainer1);
        Trainer createdTrainer2 = trainerService.create(trainer2);

        // Then
        assertEquals("John.Doe", createdTrainer1.getUsername());
        assertEquals("John.Doe.1", createdTrainer2.getUsername());
    }

    @Test
    void testFindAll() {
        // Given
        Trainer trainer1 = buildTrainer();
        Trainer trainer2 = buildTrainer();
        trainer2.setFirstName("Jane");

        // When
        trainerService.create(trainer1);
        trainerService.create(trainer2);
        List<Trainer> Trainers = trainerService.findAll();

        // Then
        assertEquals(2, Trainers.size());
        assertTrue(Trainers.stream().anyMatch(t -> t.getFirstName().equals("John")));
        assertTrue(Trainers.stream().anyMatch(t -> t.getFirstName().equals("Jane")));
    }

    @Test
    void testFindById() {
        // Given
        Trainer Trainer = buildTrainer();
        Trainer createdTrainer = trainerService.create(Trainer);

        // When
        Trainer foundTrainer = trainerService.findById(createdTrainer.getUserId());

        // Then
        assertNotNull(foundTrainer);
        assertEquals(createdTrainer.getUserId(), foundTrainer.getUserId());
        assertEquals("John.Doe", foundTrainer.getUsername());
    }

    @Test
    void testFindByIdNonExistent() {
        // When
        Trainer foundTrainer = trainerService.findById(UUID.randomUUID());

        // Then
        assertNull(foundTrainer);
    }

    @Test
    void testUpdate() {
        // Given
        Trainer trainer = buildTrainer();
        Trainer createdTrainer = trainerService.create(trainer);

        // When
        createdTrainer.setFirstName("Updated");
        createdTrainer.setLastName("Name");
        Trainer updatedTrainer = trainerService.update(createdTrainer);

        // Then
        assertEquals("Updated.Name", updatedTrainer.getUsername());
        assertEquals("Updated", updatedTrainer.getFirstName());
        assertEquals("Name", updatedTrainer.getLastName());
        assertEquals(createdTrainer.getUserId(), updatedTrainer.getUserId());
    }

    @Test
    void testDelete() {
        // Given
        Trainer trainer = buildTrainer();
        Trainer createdTrainer = trainerService.create(trainer);

        // When
        trainerService.delete(createdTrainer);

        // Then
        assertEquals(0, trainerService.findAll().size());
        assertNull(trainerService.findById(createdTrainer.getUserId()));
    }

    @Test
    void testMultipleOperations() {
        // Create
        Trainer trainer1 = buildTrainer();
        Trainer createdTrainer1 = trainerService.create(trainer1);

        // Update
        createdTrainer1.setSpecialization("Cardio");
        Trainer updatedTrainer1 = trainerService.update(createdTrainer1);

        // Create another
        Trainer trainer2 = buildTrainer();
        trainer2.setFirstName("Jane");
        Trainer createdTrainer2 = trainerService.create(trainer2);

        // Find
        List<Trainer> trainers = trainerService.findAll();
        Trainer foundTrainer = trainerService.findById(createdTrainer1.getUserId());

        // Delete
        trainerService.delete(createdTrainer1);

        // Assertions
        assertEquals(2, trainers.size());
        assertEquals("Cardio", updatedTrainer1.getSpecialization());
        assertEquals("Jane.Doe", createdTrainer2.getUsername());
        assertEquals("Cardio", foundTrainer.getSpecialization());
        assertEquals(1, trainerService.findAll().size());
    }

    private Trainer buildTrainer() {
        return new Trainer("John", "Doe", "Strength Training", true);
    }
}