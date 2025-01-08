package com.epam.spring.service;

import com.epam.spring.config.AppConfig;
import com.epam.spring.model.Trainee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(AppConfig.class)
class TraineeServiceTest {

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private SessionFactory sessionFactory;

    private Trainee trainee1;
    private Trainee trainee2;

    @BeforeEach
    void setUp() {
        trainee1 = buildTrainee("John", "Doe");
        trainee2 = buildTrainee("Will", "Salas");
    }

    @AfterEach
    void tearDown() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createMutationQuery("DELETE FROM Trainee").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    void testCreate() {
        Trainee createdTrainee = traineeService.create(trainee1);

        assertNotNull(createdTrainee.getId());
        assertEquals("John.Doe", createdTrainee.getUsername());
        assertEquals(1, traineeService.findAll().size());
    }

    @Test
    void testCreateWithExistingName() {
        Trainee createdTrainee1 = traineeService.create(trainee1);
        trainee2.setFirstName("John");
        trainee2.setLastName("Doe");
        Trainee createdTrainee2 = traineeService.create(trainee2);

        assertEquals("John.Doe", createdTrainee1.getUsername());
        assertEquals("John.Doe.1", createdTrainee2.getUsername());
    }

    @Test
    void testFindAll() {
        Trainee createdTrainee1 = traineeService.create(trainee1);
        Trainee createdTrainee2 = traineeService.create(trainee2);

        List<Trainee> trainees = traineeService.findAll();

        assertEquals(2, trainees.size());
        assertTrue(trainees.stream().anyMatch(t -> t.getUsername().equals(createdTrainee1.getUsername())));
        assertTrue(trainees.stream().anyMatch(t -> t.getUsername().equals(createdTrainee2.getUsername())));
    }

    @Test
    void testFindById() {
        Trainee createdTrainee = traineeService.create(trainee1);

        Trainee foundTrainee = traineeService.findById(createdTrainee.getId());

        assertNotNull(foundTrainee);
        assertEquals(createdTrainee.getId(), foundTrainee.getId());
        assertEquals(trainee1.getUsername(), foundTrainee.getUsername());
    }

    @Test
    void testFindByIdNonExistent() {
        long nonExistentId = 100L;
        assertThrows(RuntimeException.class, () -> traineeService.findById(nonExistentId), "Trainee with id " + nonExistentId + " not found");
    }

    @Test
    void testUpdate() {
        Trainee createdTrainee = traineeService.create(trainee1);

        createdTrainee.setFirstName("Updated");
        createdTrainee.setLastName("Name");
        Trainee updatedTrainee = traineeService.update(createdTrainee);

        assertEquals("Updated", updatedTrainee.getFirstName());
        assertEquals("Name", updatedTrainee.getLastName());
        assertEquals("Updated.Name", updatedTrainee.getUsername());
        assertEquals(createdTrainee.getId(), updatedTrainee.getId());
        assertEquals(1, traineeService.findAll().size());
    }

    @Test
    void whenUpdateNonExistingTraineeThenThrowException() {
        long id = 10L;
        trainee1.setId(id);
        assertThrows(NoSuchElementException.class, () -> traineeService.update(trainee1), "Trainee with id " + id + " not found");
    }

    @Test
    void testDelete() {
        Trainee createdTrainee = traineeService.create(trainee1);
        long id = createdTrainee.getId();

        traineeService.delete(createdTrainee);

        assertEquals(0, traineeService.findAll().size());
        assertThrows(RuntimeException.class, () -> traineeService.findById(id), "Trainee with id " + id + " not found");
    }

    @Test
    void testDeleteNonExistentTrainee() {
        assertDoesNotThrow(() -> traineeService.delete(trainee1));
    }

    @Test
    void testFindByUsername() {
        Trainee createdTrainee = traineeService.create(trainee1);

        Trainee trainee = traineeService.findByUsername(trainee1.getUsername());

        assertEquals(createdTrainee.getId(), trainee.getId());
    }

    @Test
    void testActivateShouldNotBeIdempotent() {
        Trainee createdTrainee = traineeService.create(trainee1);

        traineeService.activate(createdTrainee);

        assertFalse(createdTrainee.isActive());

        traineeService.activate(createdTrainee);

        assertTrue(createdTrainee.isActive());
    }

    @Test
    void testDeleteByUsername() {
        Trainee createdTrainee = traineeService.create(trainee1);
        long id = createdTrainee.getId();

        traineeService.deleteByUsername(createdTrainee.getUsername());

        assertEquals(0, traineeService.findAll().size());
        assertThrows(RuntimeException.class, () -> traineeService.findById(id), "Trainer with id " + id + " not found");
    }

    @Test
    void testChangePassword() {
        String newPassword = "1111111111";
        Trainee trainee = traineeService.create(trainee1);
        String username = trainee.getUsername();

        traineeService.changePassword(username, trainee.getPassword(), newPassword);

        assertEquals(newPassword, traineeService.findByUsername(username).getPassword());
    }

    @Test
    void whenChangePasswordWithIncorrectOldPasswordThenThrowException() {
        String oldPassword = "1111111111";
        String newPassword = "1132211111";
        Trainee trainee = traineeService.create(trainee1);
        String username = trainee.getUsername();

        assertThrows(RuntimeException.class, () -> traineeService.changePassword(username, oldPassword, newPassword), "Incorrect password");
    }

    @Test
    void testMultipleOperations() {
        Trainee createdTrainee1 = traineeService.create(trainee1);

        createdTrainee1.setAddress("New Address");
        Trainee updatedTrainee1 = traineeService.update(createdTrainee1);

        Trainee createdTrainee2 = traineeService.create(trainee2);

        List<Trainee> trainees = traineeService.findAll();
        Trainee foundTrainee = traineeService.findById(createdTrainee1.getId());

        traineeService.delete(createdTrainee1);

        assertEquals(2, trainees.size());
        assertEquals("New Address", updatedTrainee1.getAddress());
        assertEquals("Will.Salas", createdTrainee2.getUsername());
        assertEquals("New Address", foundTrainee.getAddress());
        assertEquals(1, traineeService.findAll().size());
    }

    private Trainee buildTrainee(String firstName, String lastName) {
        return Trainee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dataOfBirth(LocalDate.now().minusYears(25))
                .address("Test Address")
                .isActive(true)
                .build();
    }
}