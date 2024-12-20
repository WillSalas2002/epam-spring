package com.epam.spring.service;

import com.epam.spring.config.AppConfig;
import com.epam.spring.model.Trainee;
import com.epam.spring.utils.StorageClearer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(AppConfig.class)
class TraineeServiceTest {

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private StorageClearer storageClearer;

    @BeforeEach
    void setUp() {
        storageClearer.clear();
    }

    @Test
    void testCreate() {
        Trainee trainee = buildTrainee();

        Trainee createdTrainee = traineeService.create(trainee);

        assertNotNull(createdTrainee.getUuid());
        assertEquals("John.Doe", createdTrainee.getUsername());
        assertTrue(traineeService.findAll().contains(createdTrainee));
    }

    @Test
    void testCreateWithExistingName() {
        Trainee trainee1 = buildTrainee();
        Trainee trainee2 = buildTrainee();

        Trainee createdTrainee1 = traineeService.create(trainee1);
        Trainee createdTrainee2 = traineeService.create(trainee2);

        assertEquals("John.Doe", createdTrainee1.getUsername());
        assertEquals("John.Doe.1", createdTrainee2.getUsername());
    }

    @Test
    void testFindAll() {
        Trainee trainee1 = buildTrainee();
        Trainee trainee2 = buildTrainee();
        trainee2.setFirstName("Jane");

        traineeService.create(trainee1);
        traineeService.create(trainee2);
        List<Trainee> trainees = traineeService.findAll();

        assertEquals(2, trainees.size());
        assertTrue(trainees.stream().anyMatch(t -> t.getFirstName().equals("John")));
        assertTrue(trainees.stream().anyMatch(t -> t.getFirstName().equals("Jane")));
    }

    @Test
    void testFindById() {
        Trainee trainee = buildTrainee();
        Trainee createdTrainee = traineeService.create(trainee);

        Trainee foundTrainee = traineeService.findById(createdTrainee.getUuid());

        assertNotNull(foundTrainee);
        assertEquals(createdTrainee.getUuid(), foundTrainee.getUuid());
        assertEquals("John.Doe", foundTrainee.getUsername());
    }

    @Test
    void testFindByIdNonExistent() {
        Trainee foundTrainee = traineeService.findById(UUID.randomUUID());

        assertNull(foundTrainee);
    }

    @Test
    void testUpdate() {
        Trainee trainee = buildTrainee();
        Trainee createdTrainee = traineeService.create(trainee);

        createdTrainee.setFirstName("Updated");
        createdTrainee.setLastName("Name");
        Trainee updatedTrainee = traineeService.update(createdTrainee);

        assertEquals("Updated.Name", updatedTrainee.getUsername());
        assertEquals("Updated", updatedTrainee.getFirstName());
        assertEquals("Name", updatedTrainee.getLastName());
        assertEquals(createdTrainee.getUuid(), updatedTrainee.getUuid());
    }

    @Test
    void testDelete() {
        Trainee trainee = buildTrainee();
        Trainee createdTrainee = traineeService.create(trainee);

        traineeService.delete(createdTrainee);

        assertEquals(0, traineeService.findAll().size());
        assertNull(traineeService.findById(createdTrainee.getUuid()));
    }

    @Test
    void testDeleteNonExistentTrainee() {
        Trainee trainee = buildTrainee();

        assertDoesNotThrow(() -> traineeService.delete(trainee));
    }

    @Test
    void testMultipleOperations() {
        Trainee trainee1 = buildTrainee();
        Trainee createdTrainee1 = traineeService.create(trainee1);

        createdTrainee1.setAddress("New Address");
        Trainee updatedTrainee1 = traineeService.update(createdTrainee1);

        Trainee trainee2 = buildTrainee();
        trainee2.setFirstName("Jane");
        Trainee createdTrainee2 = traineeService.create(trainee2);

        List<Trainee> trainees = traineeService.findAll();
        Trainee foundTrainee = traineeService.findById(createdTrainee1.getUuid());

        traineeService.delete(createdTrainee1);

        assertEquals(2, trainees.size());
        assertEquals("New Address", updatedTrainee1.getAddress());
        assertEquals("Jane.Doe", createdTrainee2.getUsername());
        assertEquals("New Address", foundTrainee.getAddress());
        assertEquals(1, traineeService.findAll().size());
    }

    private Trainee buildTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setDataOfBirth(LocalDate.now().minusYears(25));
        trainee.setAddress("Test Address");
        trainee.setActive(true);
        return trainee;
    }
}