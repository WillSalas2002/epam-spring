package com.epam.spring.dao;

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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(AppConfig.class)
class TraineeDAOTest {

    @Autowired
    private TraineeDAO traineeDAO;

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
        Trainee createdTrainee = traineeDAO.create(trainee);

        // Then
        assertNotNull(createdTrainee.getUserId());
        assertTrue(inMemoryStorage.findAllTrainees().contains(createdTrainee));
    }

    @Test
    void testFindAll() {
        // Given
        Trainee trainee1 = buildTrainee();
        Trainee trainee2 = buildTrainee();
        trainee2.setFirstName("Jane");

        // When
        traineeDAO.create(trainee1);
        traineeDAO.create(trainee2);
        List<Trainee> trainees = traineeDAO.findAll();

        // Then
        assertEquals(2, trainees.size());
        assertTrue(trainees.stream().anyMatch(t -> t.getFirstName().equals("John")));
        assertTrue(trainees.stream().anyMatch(t -> t.getFirstName().equals("Jane")));
    }

    @Test
    void testFindById() {
        // Given
        Trainee trainee = buildTrainee();
        Trainee createdTrainee = traineeDAO.create(trainee);

        // When
        Trainee foundTrainee = traineeDAO.findById(createdTrainee.getUserId());

        // Then
        assertNotNull(foundTrainee);
        assertEquals(createdTrainee.getUserId(), foundTrainee.getUserId());
    }

    @Test
    void testFindByIdNonExistent() {
        // When
        Trainee foundTrainee = traineeDAO.findById(UUID.randomUUID());

        // Then
        assertNull(foundTrainee);
    }

    @Test
    void testUpdate() {
        // Given
        Trainee trainee = buildTrainee();
        Trainee createdTrainee = traineeDAO.create(trainee);

        // When
        createdTrainee.setFirstName("Updated");
        createdTrainee.setLastName("Name");
        Trainee updatedTrainee = traineeDAO.update(createdTrainee);

        // Then
        assertEquals("Updated", updatedTrainee.getFirstName());
        assertEquals("Name", updatedTrainee.getLastName());
        assertEquals(createdTrainee.getUserId(), updatedTrainee.getUserId());
    }

    @Test
    void testDelete() {
        // Given
        Trainee trainee = buildTrainee();
        Trainee createdTrainee = traineeDAO.create(trainee);

        // When
        traineeDAO.delete(createdTrainee);

        // Then
        assertEquals(0, traineeDAO.findAll().size());
        assertNull(traineeDAO.findById(createdTrainee.getUserId()));
    }

    @Test
    void testDeleteNonExistentTrainee() {
        // Given
        Trainee trainee = buildTrainee();

        // When & Then
        assertDoesNotThrow(() -> traineeDAO.delete(trainee));
    }

    @Test
    void testMultipleOperations() {
        // Create
        Trainee trainee1 = buildTrainee();
        Trainee createdTrainee1 = traineeDAO.create(trainee1);

        // Update
        createdTrainee1.setAddress("New Address");
        Trainee updatedTrainee1 = traineeDAO.update(createdTrainee1);

        // Create another
        Trainee trainee2 = buildTrainee();
        trainee2.setFirstName("Jane");
        Trainee createdTrainee2 = traineeDAO.create(trainee2);

        // Find
        List<Trainee> trainees = traineeDAO.findAll();
        Trainee foundTrainee = traineeDAO.findById(createdTrainee1.getUserId());

        // Delete
        traineeDAO.delete(createdTrainee1);

        // Assertions
        assertEquals(2, trainees.size());
        assertEquals("New Address", updatedTrainee1.getAddress());
        assertEquals("New Address", foundTrainee.getAddress());
        assertEquals(1, traineeDAO.findAll().size());
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