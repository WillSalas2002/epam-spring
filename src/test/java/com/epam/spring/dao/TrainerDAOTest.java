package com.epam.spring.dao;

import com.epam.spring.config.AppConfig;
import com.epam.spring.model.Trainer;
import com.epam.spring.utils.StorageClearer;
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
    private StorageClearer storageClearer;

    @BeforeEach
    void setUp() {
        storageClearer.clear();
    }

    @Test
    void testCreate() {
        Trainer trainer = buildTrainer();

        Trainer createdTrainer = trainerDAO.create(trainer);

        assertNotNull(createdTrainer.getUuid());
        assertTrue(trainerDAO.findAll().contains(createdTrainer));
    }

    @Test
    void testCreateDuplicateUsernames() {
        Trainer trainer = buildTrainer();
        Trainer trainer1 = buildTrainer();

        Trainer createdTrainer = trainerDAO.create(trainer);
        Trainer createdTrainer1 = trainerDAO.create(trainer1);

        assertNotNull(createdTrainer.getUuid());
        assertEquals(createdTrainer.getUsername(), "John.Doe");
        assertEquals(createdTrainer1.getUsername(), "John.Doe.1");
        assertTrue(trainerDAO.findAll().contains(createdTrainer));
        assertTrue(trainerDAO.findAll().contains(createdTrainer1));
    }


    @Test
    void testFindAll() {
        Trainer trainer1 = buildTrainer();
        Trainer trainer2 = buildTrainer();
        trainer2.setFirstName("Jane");

        trainerDAO.create(trainer1);
        trainerDAO.create(trainer2);
        List<Trainer> trainers = trainerDAO.findAll();

        assertEquals(2, trainers.size());
        assertTrue(trainers.stream().anyMatch(t -> t.getFirstName().equals("John")));
        assertTrue(trainers.stream().anyMatch(t -> t.getFirstName().equals("Jane")));
    }

    @Test
    void testFindById() {
        Trainer trainer = buildTrainer();
        Trainer createdTrainer = trainerDAO.create(trainer);

        Trainer foundTrainer = trainerDAO.findById(createdTrainer.getUuid());

        assertNotNull(foundTrainer);
        assertEquals(createdTrainer.getUuid(), foundTrainer.getUuid());
    }

    @Test
    void testFindByIdNonExistent() {
        Trainer foundTrainer = trainerDAO.findById(UUID.randomUUID());

        assertNull(foundTrainer);
    }

    @Test
    void testUpdate() {
        Trainer trainer = buildTrainer();
        Trainer createdTrainer = trainerDAO.create(trainer);

        createdTrainer.setFirstName("Updated");
        createdTrainer.setLastName("Name");
        Trainer updatedTrainer = trainerDAO.update(createdTrainer);

        assertEquals("Updated", updatedTrainer.getFirstName());
        assertEquals("Name", updatedTrainer.getLastName());
        assertEquals(createdTrainer.getUuid(), updatedTrainer.getUuid());
    }

    @Test
    void testDelete() {
        Trainer trainer = buildTrainer();
        Trainer createdTrainer = trainerDAO.create(trainer);

        trainerDAO.delete(createdTrainer);

        assertEquals(0, trainerDAO.findAll().size());
        assertNull(trainerDAO.findById(createdTrainer.getUuid()));
    }

    @Test
    void testDeleteNonExistentTrainee() {
        Trainer trainer = buildTrainer();

        assertDoesNotThrow(() -> trainerDAO.delete(trainer));
    }

    @Test
    void testMultipleOperations() {
        Trainer trainer1 = buildTrainer();
        Trainer createdTrainer1 = trainerDAO.create(trainer1);

        createdTrainer1.setSpecialization("Cardio");
        Trainer updatedTrainer1 = trainerDAO.update(createdTrainer1);

        Trainer trainer2 = buildTrainer();
        trainer2.setFirstName("Jane");
        trainerDAO.create(trainer2);

        List<Trainer> trainers = trainerDAO.findAll();
        Trainer foundTrainer = trainerDAO.findById(createdTrainer1.getUuid());

        trainerDAO.delete(createdTrainer1);

        assertEquals(2, trainers.size());
        assertEquals("Cardio", updatedTrainer1.getSpecialization());
        assertEquals("Cardio", foundTrainer.getSpecialization());
        assertEquals(1, trainerDAO.findAll().size());
    }

    private Trainer buildTrainer() {
        return new Trainer("John", "Doe", "Strength Training", true);
    }
}