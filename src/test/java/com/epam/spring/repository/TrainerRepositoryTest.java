package com.epam.spring.repository;

import com.epam.spring.config.AppConfig;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.TrainingType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(AppConfig.class)
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
            transaction.commit();
        }
    }

    @Test
    void testCreate() {
        Trainer createdTrainer = trainerRepository.create(trainer1);

        assertNotNull(createdTrainer.getId());
        assertEquals("John", createdTrainer.getFirstName());
        assertEquals("Doe", createdTrainer.getLastName());
    }

    @Test
    void testFindAll() {
        trainerRepository.create(trainer1);
        trainerRepository.create(trainer2);

        List<Trainer> trainers = trainerRepository.findAll();

        assertEquals(2, trainers.size());
        assertTrue(trainers.stream().anyMatch(t -> t.getFirstName().equals(trainer1.getFirstName())));
        assertTrue(trainers.stream().anyMatch(t -> t.getFirstName().equals(trainer2.getFirstName())));
    }

    @Test
    void testFindById() {
        Trainer createdTrainer = trainerRepository.create(trainer1);

        Trainer foundTrainer = trainerRepository.findById(createdTrainer.getId());

        assertNotNull(foundTrainer);
        assertEquals(createdTrainer.getId(), foundTrainer.getId());
    }

    @Test
    void testFindByIdNonExistent() {
        Trainer foundTrainer = trainerRepository.findById(10L);

        assertNull(foundTrainer);
    }

    @Test
    void testUpdate() {
        Trainer createdTrainer = trainerRepository.create(trainer1);

        createdTrainer.setFirstName("Updated");
        createdTrainer.setLastName("Name");
        Trainer updatedTrainer = trainerRepository.update(createdTrainer);

        assertEquals(createdTrainer.getId(), updatedTrainer.getId());
        assertEquals("Updated", updatedTrainer.getFirstName());
        assertEquals("Name", updatedTrainer.getLastName());
    }

    @Test
    void testDelete() {
        Trainer createdTrainer = trainerRepository.create(trainer1);

        trainerRepository.delete(createdTrainer);

        assertEquals(0, trainerRepository.findAll().size());
        assertNull(trainerRepository.findById(createdTrainer.getId()));
    }

    @Test
    void testDeleteNonExistentTrainee() {
        assertDoesNotThrow(() -> trainerRepository.delete(trainer1));
    }

    @Test
    void testMultipleOperations() {
        Trainer createdTrainer1 = trainerRepository.create(trainer1);

        String expectedName = "Adam";
        createdTrainer1.setFirstName(expectedName);
        Trainer updatedTrainer1 = trainerRepository.update(createdTrainer1);

        trainerRepository.create(trainer2);

        List<Trainer> trainers = trainerRepository.findAll();
        Trainer foundTrainer = trainerRepository.findById(createdTrainer1.getId());

        trainerRepository.delete(createdTrainer1);

        assertEquals(2, trainers.size());
        assertEquals(expectedName, updatedTrainer1.getFirstName());
        assertEquals(expectedName, foundTrainer.getFirstName());
        assertEquals(1, trainerRepository.findAll().size());
    }

    private Trainer buildTrainer(String firstName, String lastName) {
        return Trainer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(firstName + "." + lastName)
                .password("1111111111")
                .specialization(buildTrainingType())
                .build();
    }

    private static TrainingType buildTrainingType() {
        return TrainingType.builder()
                .trainingTypeName("Cardio")
                .build();
    }
}
