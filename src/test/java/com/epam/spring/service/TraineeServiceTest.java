package com.epam.spring.service;

import com.epam.spring.config.AppConfig;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.User;
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
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    void testCreate() {
        Trainee createdTrainee = traineeService.create(trainee1);

        assertNotNull(createdTrainee.getId());
        assertEquals("John.Doe", createdTrainee.getUser().getUsername());
        assertEquals(1, traineeService.findAll().size());
    }

    @Test
    void testCreateWithExistingName() {
        Trainee createdTrainee1 = traineeService.create(trainee1);
        trainee2.getUser().setFirstName("John");
        trainee2.getUser().setLastName("Doe");
        Trainee createdTrainee2 = traineeService.create(trainee2);

        assertEquals("John.Doe", createdTrainee1.getUser().getUsername());
        assertEquals("John.Doe.1", createdTrainee2.getUser().getUsername());
    }

    @Test
    void testFindAll() {
        Trainee createdTrainee1 = traineeService.create(trainee1);
        Trainee createdTrainee2 = traineeService.create(trainee2);

        List<Trainee> trainees = traineeService.findAll();

        assertEquals(2, trainees.size());
        assertTrue(trainees.stream().anyMatch(t -> t.getUser().getUsername().equals(createdTrainee1.getUser().getUsername())));
        assertTrue(trainees.stream().anyMatch(t -> t.getUser().getUsername().equals(createdTrainee2.getUser().getUsername())));
    }

    @Test
    void testFindById() {
        Trainee createdTrainee = traineeService.create(trainee1);

        Trainee foundTrainee = traineeService.findById(createdTrainee.getId());

        assertNotNull(foundTrainee);
        assertEquals(createdTrainee.getId(), foundTrainee.getId());
        assertEquals(trainee1.getUser().getUsername(), foundTrainee.getUser().getUsername());
    }

    @Test
    void testFindByIdNonExistent() {
        long nonExistentId = 100L;
        assertThrows(RuntimeException.class, () -> traineeService.findById(nonExistentId), "Trainee with id " + nonExistentId + " not found");
    }

    @Test
    void testUpdate() {
        Trainee createdTrainee = traineeService.create(trainee1);

        createdTrainee.getUser().setFirstName("Updated");
        createdTrainee.getUser().setLastName("Name");
        Trainee updatedTrainee = traineeService.update(createdTrainee);

        assertEquals("Updated", updatedTrainee.getUser().getFirstName());
        assertEquals("Name", updatedTrainee.getUser().getLastName());
        assertEquals("Updated.Name", updatedTrainee.getUser().getUsername());
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

        Trainee trainee = traineeService.findByUsername(trainee1.getUser().getUsername());

        assertEquals(createdTrainee.getId(), trainee.getId());
    }

    @Test
    void testActivateShouldNotBeIdempotent() {
        Trainee createdTrainee = traineeService.create(trainee1);

        traineeService.activate(createdTrainee);

        assertFalse(createdTrainee.getUser().isActive());

        traineeService.activate(createdTrainee);

        assertTrue(createdTrainee.getUser().isActive());
    }

    @Test
    void testDeleteByUsername() {
        Trainee createdTrainee = traineeService.create(trainee1);
        long id = createdTrainee.getId();

        traineeService.deleteByUsername(createdTrainee.getUser().getUsername());

        assertEquals(0, traineeService.findAll().size());
        assertThrows(RuntimeException.class, () -> traineeService.findById(id), "Trainer with id " + id + " not found");
    }

    @Test
    void testChangePassword() {
        String newPassword = "1111111111";
        Trainee trainee = traineeService.create(trainee1);
        String username = trainee.getUser().getUsername();

        traineeService.changePassword(username, trainee.getUser().getPassword(), newPassword);

        assertEquals(newPassword, traineeService.findByUsername(username).getUser().getPassword());
    }

    @Test
    void whenChangePasswordWithIncorrectOldPasswordThenThrowException() {
        String oldPassword = "1111111111";
        String newPassword = "1132211111";
        Trainee trainee = traineeService.create(trainee1);
        String username = trainee.getUser().getUsername();

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
        assertEquals("Will.Salas", createdTrainee2.getUser().getUsername());
        assertEquals("New Address", foundTrainee.getAddress());
        assertEquals(1, traineeService.findAll().size());
    }

    private Trainee buildTrainee(String firstName, String lastName) {
        return Trainee.builder()
                .user(User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .isActive(true)
                        .build()
                )
                .dataOfBirth(LocalDate.now().minusYears(25))
                .address("Test Address")
                .build();
    }
}