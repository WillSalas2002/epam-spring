package com.epam.spring.repository;

import com.epam.spring.config.TestConfig;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.TrainingType;
import com.epam.spring.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
class TrainerRepositoryTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private SessionFactory sessionFactory;

    private Trainer trainer1;
    private Trainer trainer2;

    @BeforeEach
    void setUp() {
        trainer1 = buildTrainer("John", "Doe");
        trainer2 = buildTrainer("Will", "Salas");
    }

    @AfterEach
    void tearDown() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createMutationQuery("DELETE FROM Trainer").executeUpdate();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    void testCreate() {
        Trainer createdTrainer = trainerRepository.create(trainer1);

        assertNotNull(createdTrainer.getId());
        assertNotNull(createdTrainer.getUser().getId());
        assertEquals("John", createdTrainer.getUser().getFirstName());
        assertEquals("Doe", createdTrainer.getUser().getLastName());
    }

    @Test
    void testFindAll() {
        trainerRepository.create(trainer1);
        trainerRepository.create(trainer2);

        List<Trainer> trainers = trainerRepository.findAll();

        assertEquals(2, trainers.size());
        assertTrue(trainers.stream().anyMatch(t -> t.getUser().getFirstName().equals(trainer1.getUser().getFirstName())));
        assertTrue(trainers.stream().anyMatch(t -> t.getUser().getFirstName().equals(trainer2.getUser().getFirstName())));
    }

    @Test
    void testFindById() {
        Trainer createdTrainer = trainerRepository.create(trainer1);

        Optional<Trainer> foundTrainerOptional = trainerRepository.findById(createdTrainer.getId());

        assertTrue(foundTrainerOptional.isPresent());
        assertEquals(createdTrainer.getId(), foundTrainerOptional.get().getId());
    }

    @Test
    void testFindByIdNonExistent() {
        Optional<Trainer> foundTrainerOptional = trainerRepository.findById(10L);

        assertTrue(foundTrainerOptional.isEmpty());
    }

    @Test
    void testUpdate() {
        Trainer createdTrainer = trainerRepository.create(trainer1);

        createdTrainer.getUser().setFirstName("Updated");
        createdTrainer.getUser().setLastName("Name");
        Trainer updatedTrainer = trainerRepository.update(createdTrainer);

        assertEquals(createdTrainer.getUser().getId(), updatedTrainer.getUser().getId());
        assertEquals("Updated", updatedTrainer.getUser().getFirstName());
        assertEquals("Name", updatedTrainer.getUser().getLastName());
    }

    @Test
    void testDelete() {
        Trainer createdTrainer = trainerRepository.create(trainer1);

        trainerRepository.delete(createdTrainer);

        assertEquals(0, trainerRepository.findAll().size());
    }

    @Test
    void testDeleteNonExistentTrainee() {
        assertDoesNotThrow(() -> trainerRepository.delete(trainer1));
    }

    @Test
    void testMultipleOperations() {
        Trainer createdTrainer1 = trainerRepository.create(trainer1);

        String expectedName = "Adam";
        createdTrainer1.getUser().setFirstName(expectedName);
        Trainer updatedTrainer1 = trainerRepository.update(createdTrainer1);

        trainerRepository.create(trainer2);

        List<Trainer> trainers = trainerRepository.findAll();
        Optional<Trainer> foundTrainerOptional = trainerRepository.findById(createdTrainer1.getId());

        trainerRepository.delete(createdTrainer1);

        assertTrue(foundTrainerOptional.isPresent());
        assertEquals(2, trainers.size());
        assertEquals(expectedName, updatedTrainer1.getUser().getFirstName());
        assertEquals(expectedName, foundTrainerOptional.get().getUser().getFirstName());
        assertEquals(1, trainerRepository.findAll().size());
    }

    private Trainer buildTrainer(String firstName, String lastName) {
        return Trainer.builder()
                .user(User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .username(firstName + "." + lastName)
                        .password("1111111111")
                        .isActive(true)
                        .build()
                )
                .specialization(buildTrainingType())
                .build();
    }

    private static TrainingType buildTrainingType() {
        return TrainingType.builder()
                .id(1L)
                .trainingTypeName("Cardio")
                .build();
    }
}
