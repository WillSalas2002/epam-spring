package com.epam.spring.dao;

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
class TraineeDAOTest {

    @Autowired
    private TraineeDAO traineeDAO;

    @Autowired
    private StorageClearer storageClearer;

    @BeforeEach
    void setUp() {
        storageClearer.clear();
    }

    @Test
    void testCreate() {
        Trainee trainee = buildTrainee();

        Trainee createdTrainee = traineeDAO.create(trainee);

        assertNotNull(createdTrainee.getUuid());
        assertEquals("John.Doe", createdTrainee.getUsername());
        assertEquals(10, createdTrainee.getPassword().length());
        assertTrue(traineeDAO.findAll().contains(createdTrainee));
    }

    @Test
    void testCreateDuplicateUsernames() {
        Trainee trainee = buildTrainee();
        Trainee trainee1 = buildTrainee();

        Trainee createdTrainee = traineeDAO.create(trainee);
        Trainee createdTrainee1 = traineeDAO.create(trainee1);

        assertNotNull(createdTrainee.getUuid());
        assertEquals(createdTrainee.getUsername(), "John.Doe");
        assertEquals(createdTrainee1.getUsername(), "John.Doe.1");
        assertTrue(traineeDAO.findAll().contains(createdTrainee));
    }

    @Test
    void testFindAll() {
        Trainee trainee1 = buildTrainee();
        Trainee trainee2 = buildTrainee();
        trainee2.setFirstName("Jane");

        traineeDAO.create(trainee1);
        traineeDAO.create(trainee2);
        List<Trainee> trainees = traineeDAO.findAll();

        assertEquals(2, trainees.size());
        assertTrue(trainees.stream().anyMatch(t -> t.getFirstName().equals("John")));
        assertTrue(trainees.stream().anyMatch(t -> t.getFirstName().equals("Jane")));
    }

    @Test
    void testFindById() {
        Trainee trainee = buildTrainee();
        Trainee createdTrainee = traineeDAO.create(trainee);

        Trainee foundTrainee = traineeDAO.findById(createdTrainee.getUuid());

        assertNotNull(foundTrainee);
        assertEquals(createdTrainee.getUuid(), foundTrainee.getUuid());
    }

    @Test
    void testFindByIdNonExistent() {
        Trainee foundTrainee = traineeDAO.findById(UUID.randomUUID());

        assertNull(foundTrainee);
    }

    @Test
    void testUpdate() {
        Trainee trainee = buildTrainee();
        Trainee createdTrainee = traineeDAO.create(trainee);

        createdTrainee.setFirstName("Updated");
        createdTrainee.setLastName("Name");
        Trainee updatedTrainee = traineeDAO.update(createdTrainee);

        assertEquals("Updated", updatedTrainee.getFirstName());
        assertEquals("Name", updatedTrainee.getLastName());
        assertEquals(createdTrainee.getUuid(), updatedTrainee.getUuid());
    }

    @Test
    void whenUsernameUpdatedToExistingUsername_thenShouldHaveSerialAdded() {
        Trainee trainee1 = buildTrainee();
        trainee1.setFirstName("Will");
        trainee1.setLastName("Salas");
        Trainee trainee2 = buildTrainee();

        Trainee createdTrainee1 = traineeDAO.create(trainee1);
        Trainee createdTrainee2 = traineeDAO.create(trainee2);

        createdTrainee2.setFirstName("Will");
        createdTrainee2.setLastName("Salas");
        traineeDAO.update(createdTrainee2);

        assertEquals("Will.Salas", createdTrainee1.getUsername());
        assertEquals("Will.Salas.1", createdTrainee2.getUsername());
    }

    @Test
    void testDelete() {
        Trainee trainee = buildTrainee();
        Trainee createdTrainee = traineeDAO.create(trainee);

        traineeDAO.delete(createdTrainee);

        assertEquals(0, traineeDAO.findAll().size());
        assertNull(traineeDAO.findById(createdTrainee.getUuid()));
    }

    @Test
    void testDeleteNonExistentTrainee() {
        Trainee trainee = buildTrainee();

        assertDoesNotThrow(() -> traineeDAO.delete(trainee));
    }

    @Test
    void testMultipleOperations() {
        Trainee trainee1 = buildTrainee();
        Trainee createdTrainee1 = traineeDAO.create(trainee1);

        createdTrainee1.setAddress("New Address");
        Trainee updatedTrainee1 = traineeDAO.update(createdTrainee1);

        Trainee trainee2 = buildTrainee();
        trainee2.setFirstName("Jane");
        traineeDAO.create(trainee2);

        List<Trainee> trainees = traineeDAO.findAll();
        Trainee foundTrainee = traineeDAO.findById(createdTrainee1.getUuid());

        traineeDAO.delete(createdTrainee1);

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