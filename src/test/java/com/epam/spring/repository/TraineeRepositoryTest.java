package com.epam.spring.repository;

import com.epam.spring.model.Trainee;
import com.epam.spring.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class TraineeRepositoryTest {

    @Autowired
    private TraineeRepository traineeRepository;

    private Trainee trainee1;
    private Trainee trainee2;

    @BeforeEach
    void setUp() {
        trainee1 = buildTrainee("John", "Doe");
        trainee2 = buildTrainee("Will", "Salas");
    }

    @Test
    void testCreate() {
        Trainee createdTrainee = traineeRepository.save(trainee1);

        assertNotNull(createdTrainee.getId());
        assertEquals(trainee1.getUser().getFirstName(), createdTrainee.getUser().getFirstName());
        assertEquals(trainee1.getUser().getLastName(), createdTrainee.getUser().getLastName());
    }

    @Test
    void testFindAll() {
        traineeRepository.save(trainee1);
        traineeRepository.save(trainee2);

        List<Trainee> trainees = traineeRepository.findAll();

        assertEquals(2, trainees.size());
        assertTrue(trainees.stream().anyMatch(t -> t.getUser().getFirstName().equals(trainee1.getUser().getFirstName())));
        assertTrue(trainees.stream().anyMatch(t -> t.getUser().getFirstName().equals(trainee1.getUser().getFirstName())));
    }

    @Test
    void testFindById() {
        Trainee createdTrainee = traineeRepository.save(trainee1);

        Optional<Trainee> traineeByIdOptional = traineeRepository.findById(createdTrainee.getId());

        assertNotNull(createdTrainee);
        assertTrue(traineeByIdOptional.isPresent());
        assertEquals(createdTrainee.getId(), traineeByIdOptional.get().getId());
        assertEquals(createdTrainee.getUser().getFirstName(), traineeByIdOptional.get().getUser().getFirstName());
        assertEquals(createdTrainee.getUser().getLastName(), traineeByIdOptional.get().getUser().getLastName());
    }

    @Test
    void testFindByIdNonExistent() {
        Optional<Trainee> traineeByIdOptional = traineeRepository.findById(10L);

        assertFalse(traineeByIdOptional.isPresent());
    }

    @Test
    void testUpdate() {
        String expectedFirstName = "Updated";
        String expectedLastName = "Name";
        String expectedAddress = "New Address";

        Trainee createdTrainee = traineeRepository.save(trainee1);

        createdTrainee.getUser().setFirstName(expectedFirstName);
        createdTrainee.getUser().setLastName(expectedLastName);
        createdTrainee.setAddress(expectedAddress);
        Trainee updatedTrainee = traineeRepository.save(createdTrainee);

        assertEquals(createdTrainee.getId(), updatedTrainee.getId());
        assertEquals(expectedFirstName, updatedTrainee.getUser().getFirstName());
        assertEquals(expectedLastName, updatedTrainee.getUser().getLastName());
        assertEquals(expectedAddress, updatedTrainee.getAddress());
    }

    @Test
    void testDelete() {
        Trainee createdTrainee = traineeRepository.save(trainee1);

        traineeRepository.delete(createdTrainee);

        assertEquals(0, traineeRepository.findAll().size());

    }

    @Test
    void testDeleteNonExistentTrainee() {
        assertDoesNotThrow(() -> traineeRepository.delete(trainee1));
    }

    @Test
    void testMultipleOperations() {
        Trainee createdTrainee1 = traineeRepository.save(trainee1);

        createdTrainee1.setAddress("New Address");
        Trainee updatedTrainee1 = traineeRepository.save(createdTrainee1);

        traineeRepository.save(trainee2);

        List<Trainee> trainees = traineeRepository.findAll();
        Optional<Trainee> foundTraineeOptional = traineeRepository.findById(createdTrainee1.getId());

        traineeRepository.delete(createdTrainee1);

        assertTrue(foundTraineeOptional.isPresent());
        assertEquals(2, trainees.size());
        assertEquals("New Address", updatedTrainee1.getAddress());
        assertEquals("New Address", foundTraineeOptional.get().getAddress());
        assertEquals(1, traineeRepository.findAll().size());
    }

    private Trainee buildTrainee(String firstName, String lastName) {
        return Trainee.builder()
                .user(User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .username(firstName + "." + lastName)
                        .password("1111111111")
                        .isActive(true)
                        .build()
                )
                .dataOfBirth(LocalDate.now().minusYears(25))
                .address("Test Address")
                .build();
    }
}