package com.epam.spring.service;

import com.epam.spring.config.AppConfig;
import com.epam.spring.model.Trainer;
import com.epam.spring.utils.StorageClearer;
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
    private StorageClearer storageClearer;

    @BeforeEach
    void setUp() {
        storageClearer.clear();
    }

    @Test
    void testCreate() {
        Trainer Trainer = buildTrainer();

        Trainer createdTrainer = trainerService.create(Trainer);

        assertNotNull(createdTrainer.getUuid());
        assertEquals("John.Doe", createdTrainer.getUsername());
        assertTrue(trainerService.findAll().contains(createdTrainer));
    }

    @Test
    void testCreateWithExistingName() {
        Trainer trainer1 = buildTrainer();
        Trainer trainer2 = buildTrainer();

        Trainer createdTrainer1 = trainerService.create(trainer1);
        Trainer createdTrainer2 = trainerService.create(trainer2);

        assertEquals("John.Doe", createdTrainer1.getUsername());
        assertEquals("John.Doe.1", createdTrainer2.getUsername());
    }

    @Test
    void testFindAll() {
        Trainer trainer1 = buildTrainer();
        Trainer trainer2 = buildTrainer();
        trainer2.setFirstName("Jane");

        trainerService.create(trainer1);
        trainerService.create(trainer2);
        List<Trainer> Trainers = trainerService.findAll();

        assertEquals(2, Trainers.size());
        assertTrue(Trainers.stream().anyMatch(t -> t.getFirstName().equals("John")));
        assertTrue(Trainers.stream().anyMatch(t -> t.getFirstName().equals("Jane")));
    }

    @Test
    void testFindById() {
        Trainer Trainer = buildTrainer();
        Trainer createdTrainer = trainerService.create(Trainer);

        Trainer foundTrainer = trainerService.findById(createdTrainer.getUuid());

        assertNotNull(foundTrainer);
        assertEquals(createdTrainer.getUuid(), foundTrainer.getUuid());
        assertEquals("John.Doe", foundTrainer.getUsername());
    }

    @Test
    void testFindByIdNonExistent() {
        Trainer foundTrainer = trainerService.findById(UUID.randomUUID());

        assertNull(foundTrainer);
    }

    @Test
    void testUpdate() {
        Trainer trainer = buildTrainer();
        Trainer createdTrainer = trainerService.create(trainer);

        createdTrainer.setFirstName("Updated");
        createdTrainer.setLastName("Name");
        Trainer updatedTrainer = trainerService.update(createdTrainer);

        assertEquals("Updated.Name", updatedTrainer.getUsername());
        assertEquals("Updated", updatedTrainer.getFirstName());
        assertEquals("Name", updatedTrainer.getLastName());
        assertEquals(createdTrainer.getUuid(), updatedTrainer.getUuid());
    }

    @Test
    void testDelete() {
        Trainer trainer = buildTrainer();
        Trainer createdTrainer = trainerService.create(trainer);

        trainerService.delete(createdTrainer);

        assertEquals(0, trainerService.findAll().size());
        assertNull(trainerService.findById(createdTrainer.getUuid()));
    }

    @Test
    void testMultipleOperations() {
        Trainer trainer1 = buildTrainer();
        Trainer createdTrainer1 = trainerService.create(trainer1);

        createdTrainer1.setSpecialization("Cardio");
        Trainer updatedTrainer1 = trainerService.update(createdTrainer1);

        Trainer trainer2 = buildTrainer();
        trainer2.setFirstName("Jane");
        Trainer createdTrainer2 = trainerService.create(trainer2);

        List<Trainer> trainers = trainerService.findAll();
        Trainer foundTrainer = trainerService.findById(createdTrainer1.getUuid());

        trainerService.delete(createdTrainer1);

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