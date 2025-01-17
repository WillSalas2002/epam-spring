package com.epam.spring.repository;

import com.epam.spring.config.AppConfig;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import com.epam.spring.model.User;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.createMutationQuery("DELETE FROM TrainingType").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    public void testCreateTraining() {
        Trainee createdTrainee = null;
        Trainer createdTrainer = trainerService.create(trainer);
        TrainingType trainingType = createdTrainer.getSpecialization();

        Training training = buildTraining(createdTrainee, createdTrainer, trainingType);

        Training createdTraining = trainingRepository.create(training);
        Optional<Training> trainingByIdOptional = trainingRepository.findById(createdTraining.getId());

        assertNotNull(createdTraining);
        assertTrue(trainingByIdOptional.isPresent());
        assertEquals(90, trainingByIdOptional.get().getDuration());
        assertEquals(1, trainingRepository.findAll().size());

    }

    private Trainer buildTrainer(String firstName, String lastName) {
        return Trainer.builder()
                .user(User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .isActive(true)
                        .build()
                )
                .specialization(buildTrainingType())
                .build();
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

    private static TrainingType buildTrainingType() {
        return TrainingType.builder()
                .trainingTypeName("Cardio")
                .build();
    }

    private static Training buildTraining(Trainee trainee, Trainer trainer, TrainingType trainingType) {
        return Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .name("Hard Cardio")
                .trainingType(trainingType)
                .date(LocalDateTime.now().plusHours(5))
                .duration(90)
                .build();
    }
}