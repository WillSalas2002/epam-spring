package com.epam.spring.service;

import com.epam.spring.config.AppConfig;
import com.epam.spring.model.Trainee;
import com.epam.spring.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(AppConfig.class)
class TraineeServiceTest {

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private InMemoryStorage inMemoryStorage;

    @BeforeEach
    void setUp() {
        inMemoryStorage.clearDB();
    }

    @Test
    void testCreate() {
        // Given
        Trainee trainee = buildTrainee();

        // When
        Trainee createdTrainee = traineeService.create(trainee);

        // Then
        assertNotNull(createdTrainee.getUserId());
        assertEquals("John.Doe", createdTrainee.getUsername());
        assertTrue(inMemoryStorage.findAllTrainees().contains(createdTrainee));
    }

    @Test
    void testCreateWithExistingName() {
        // Given
        Trainee trainee1 = buildTrainee();
        Trainee trainee2 = buildTrainee();

        // When
        Trainee createdTrainee1 = traineeService.create(trainee1);
        Trainee createdTrainee2 = traineeService.create(trainee2);

        // Then
        assertEquals("John.Doe", createdTrainee1.getUsername());
        assertEquals("John.Doe.1", createdTrainee2.getUsername());
    }

    @Test
    void testFindAll() {
        // Given
        Trainee trainee1 = buildTrainee();
        Trainee trainee2 = buildTrainee();
        trainee2.setFirstName("Jane");

        // When
        traineeService.create(trainee1);
        traineeService.create(trainee2);
        List<Trainee> trainees = traineeService.findAll();

        // Then
        assertEquals(2, trainees.size());
        assertTrue(trainees.stream().anyMatch(t -> t.getFirstName().equals("John")));
        assertTrue(trainees.stream().anyMatch(t -> t.getFirstName().equals("Jane")));
    }

    @Test
    void testFindById() {
        // Given
        Trainee trainee = buildTrainee();
        Trainee createdTrainee = traineeService.create(trainee);

        // When
        Trainee foundTrainee = traineeService.findById(createdTrainee.getUserId());

        // Then
        assertNotNull(foundTrainee);
        assertEquals(createdTrainee.getUserId(), foundTrainee.getUserId());
        assertEquals("John.Doe", foundTrainee.getUsername());
    }

    @Test
    void testFindByIdNonExistent() {
        // When
        Trainee foundTrainee = traineeService.findById(UUID.randomUUID());

        // Then
        assertNull(foundTrainee);
    }

    @Test
    void testUpdate() {
        // Given
        Trainee trainee = buildTrainee();
        Trainee createdTrainee = traineeService.create(trainee);

        // When
        createdTrainee.setFirstName("Updated");
        createdTrainee.setLastName("Name");
        Trainee updatedTrainee = traineeService.update(createdTrainee);

        // Then
        assertEquals("Updated.Name", updatedTrainee.getUsername());
        assertEquals("Updated", updatedTrainee.getFirstName());
        assertEquals("Name", updatedTrainee.getLastName());
        assertEquals(createdTrainee.getUserId(), updatedTrainee.getUserId());
    }

    @Test
    void testDelete() {
        // Given
        Trainee trainee = buildTrainee();
        Trainee createdTrainee = traineeService.create(trainee);

        // When
        traineeService.delete(createdTrainee);

        // Then
        assertEquals(0, traineeService.findAll().size());
        assertNull(traineeService.findById(createdTrainee.getUserId()));
    }

    @Test
    void testDeleteNonExistentTrainee() {
        // Given
        Trainee trainee = buildTrainee();

        // When & Then
        assertDoesNotThrow(() -> traineeService.delete(trainee));
    }

    @Test
    void testMultipleOperations() {
        // Create
        Trainee trainee1 = buildTrainee();
        Trainee createdTrainee1 = traineeService.create(trainee1);

        // Update
        createdTrainee1.setAddress("New Address");
        Trainee updatedTrainee1 = traineeService.update(createdTrainee1);

        // Create another
        Trainee trainee2 = buildTrainee();
        trainee2.setFirstName("Jane");
        Trainee createdTrainee2 = traineeService.create(trainee2);

        // Find
        List<Trainee> trainees = traineeService.findAll();
        Trainee foundTrainee = traineeService.findById(createdTrainee1.getUserId());

        // Delete
        traineeService.delete(createdTrainee1);

        // Assertions
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