package com.epam.spring.repository;

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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(AppConfig.class)
class TraineeRepositoryTest {

    @Autowired
    private TraineeRepository traineeRepository;

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
            session.createQuery("DELETE FROM Trainee", Trainee.class).executeUpdate();
            transaction.commit();
        }
    }

    @Test
    void testCreate() {
        Trainee createdTrainee = traineeRepository.create(trainee1);

        assertNotNull(createdTrainee.getId());
        assertEquals(trainee1.getFirstName(), createdTrainee.getFirstName());
        assertEquals(trainee1.getLastName(), createdTrainee.getLastName());
    }

    @Test
    void testFindAll() {
        traineeRepository.create(trainee1);
        traineeRepository.create(trainee2);

        List<Trainee> trainees = traineeRepository.findAll();

        assertEquals(2, trainees.size());
        assertTrue(trainees.stream().anyMatch(t -> t.getFirstName().equals(trainee1.getFirstName())));
        assertTrue(trainees.stream().anyMatch(t -> t.getFirstName().equals(trainee1.getFirstName())));
    }

    @Test
    void testFindById() {
        Trainee createdTrainee = traineeRepository.create(trainee1);

        Trainee traineeById = traineeRepository.findById(createdTrainee.getId());

        assertNotNull(createdTrainee);
        assertEquals(createdTrainee.getId(), traineeById.getId());
    }

    @Test
    void testFindByIdNonExistent() {
        Trainee foundTrainee = traineeRepository.findById(10L);

        assertNull(foundTrainee);
    }

    @Test
    void testUpdate() {
        String expectedFirstName = "Updated";
        String expectedLastName = "Name";

        Trainee createdTrainee = traineeRepository.create(trainee1);

        createdTrainee.setFirstName(expectedFirstName);
        createdTrainee.setLastName(expectedLastName);
        Trainee updatedTrainee = traineeRepository.update(createdTrainee);

        assertEquals(createdTrainee.getId(), updatedTrainee.getId());
        assertEquals(expectedFirstName, updatedTrainee.getFirstName());
        assertEquals(expectedLastName, updatedTrainee.getLastName());
    }

    @Test
    void testDelete() {
        Trainee createdTrainee = traineeRepository.create(trainee1);

        traineeRepository.delete(createdTrainee);

        assertEquals(0, traineeRepository.findAll().size());
        assertNull(traineeRepository.findById(createdTrainee.getId()));
    }

    @Test
    void testDeleteNonExistentTrainee() {
        assertDoesNotThrow(() -> traineeRepository.delete(trainee1));
    }

    @Test
    void testMultipleOperations() {
        Trainee createdTrainee1 = traineeRepository.create(trainee1);

        createdTrainee1.setAddress("New Address");
        Trainee updatedTrainee1 = traineeRepository.update(createdTrainee1);

        traineeRepository.create(trainee2);

        List<Trainee> trainees = traineeRepository.findAll();
        Trainee foundTrainee = traineeRepository.findById(createdTrainee1.getId());

        traineeRepository.delete(createdTrainee1);

        assertEquals(2, trainees.size());
        assertEquals("New Address", updatedTrainee1.getAddress());
        assertEquals("New Address", foundTrainee.getAddress());
        assertEquals(1, traineeRepository.findAll().size());
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