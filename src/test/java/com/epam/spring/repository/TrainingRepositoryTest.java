package com.epam.spring.repository;

import com.epam.spring.config.AppConfig;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import com.epam.spring.service.TraineeService;
import com.epam.spring.service.TrainerService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(AppConfig.class)
class TrainingRepositoryTest {

    @Autowired
    private TrainingRepository trainingRepository;
    @Autowired
    private TraineeService traineeService;
    @Autowired
    private TrainerService trainerService;
    @Autowired
    private SessionFactory sessionFactory;

    private Trainer trainer;
    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainer = buildTrainer("Will", "Salas");
        trainee = buildTrainee("Adam", "Simpson");
    }

    @AfterEach
    void tearDown() {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createMutationQuery("DELETE FROM Training").executeUpdate();
            session.createMutationQuery("DELETE FROM Trainer").executeUpdate();
            session.createMutationQuery("DELETE FROM Trainee").executeUpdate();
            session.createMutationQuery("DELETE FROM TrainingType").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    public void testCreateTraining() {
        Trainee createdTrainee = traineeService.create(trainee);
        Trainer createdTrainer = trainerService.create(trainer);
        TrainingType trainingType = createdTrainer.getSpecialization();

        Training training = new Training(createdTrainee, createdTrainer, "Strong man training", trainingType, LocalDateTime.now().plusHours(3), 120);

        Training createdTraining = trainingRepository.create(training);
        Training trainingById = trainingRepository.findById(createdTraining.getId());

        assertNotNull(createdTraining);
        assertNotNull(trainingById);
        assertEquals(120, trainingById.getDuration());
        assertEquals(1, trainingRepository.findAll().size());

    }

    private Trainer buildTrainer(String firstName, String lastName) {
        return Trainer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .specialization(buildTrainingType())
                .build();
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

    private static TrainingType buildTrainingType() {
        return TrainingType.builder()
                .trainingTypeName("Cardio")
                .build();
    }
}