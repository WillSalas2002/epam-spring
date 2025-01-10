package com.epam.spring.service;

import com.epam.spring.config.AppConfig;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(AppConfig.class)
class TrainingServiceTest {

    @Autowired
    private TrainingService trainingService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private TraineeService traineeService;
    @Autowired
    private TrainerService trainerService;

    private Trainer trainer;
    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainer = buildTrainer("Will", "Salas");
        trainee = buildTrainee("Adam", "Sam");
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
    public void testFindTraineeAndTrainerTrainings() {
        Trainee trainee1 = buildTrainee("trainee1", "trainee1");
        traineeService.create(trainee1);
        Trainer trainer1 = buildTrainer("trainer1", "trainer1");
        trainerService.create(trainer1);
        TrainingType trainingType1 = trainer1.getSpecialization();
        Training training1 = new Training(trainee1, trainer1, "Strong man training", trainingType1, LocalDateTime.now().plusHours(3), 120);

        Trainer trainer2 = buildTrainer("trainer2", "trainer2");
        trainerService.create(trainer2);
        TrainingType trainingType2 = trainer1.getSpecialization();
        Training training2 = new Training(trainee1, trainer2, "Strong man training", trainingType2, LocalDateTime.now().plusHours(3), 120);

        trainingService.create(training1);
        trainingService.create(training2);

        List<Training> traineeTrainings = trainingService.findTraineeTrainings(trainee1.getUser().getUsername(), null, null, null, null);
        List<Training> trainerTrainings = trainingService.findTrainerTrainings(trainer1.getUser().getUsername(), null, null, null, null);

        assertEquals(2, traineeTrainings.size());
        assertEquals(1, trainerTrainings.size());

    }

    @Test
    public void testCreateTraining() {
        trainerService.create(trainer);
        traineeService.create(trainee);
        TrainingType trainingType = trainer.getSpecialization();
        Training training = new Training(trainee, trainer, "Strong man training", trainingType, LocalDateTime.now().plusHours(3), 120);

        Training createdTraining = trainingService.create(training);

        assertNotNull(createdTraining);
        assertEquals(createdTraining.getName(), trainingService.findById(createdTraining.getId()).getName());
        assertEquals(1, trainingService.findAll().size());
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
}
