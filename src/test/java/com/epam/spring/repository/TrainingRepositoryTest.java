package com.epam.spring.repository;

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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(AppConfig.class)
class TrainingRepositoryTest {

    @Autowired
    private TrainingRepository trainingRepository;
    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private TraineeRepository traineeRepository;
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
            transaction.commit();
        }
    }

    @Test
    public void testCreateTraining() {
        Trainee createdTrainee = traineeRepository.create(trainee);
        Trainer createdTrainer = trainerRepository.create(trainer);
        TrainingType trainingType = createdTrainer.getSpecialization();

        Training training = buildTraining(createdTrainee, createdTrainer, trainingType);

        Training createdTraining = trainingRepository.create(training);
        Optional<Training> trainingByIdOptional = trainingRepository.findById(createdTraining.getId());

        assertNotNull(createdTraining);
        assertTrue(trainingByIdOptional.isPresent());
        assertEquals(90, trainingByIdOptional.get().getDuration());
        assertEquals(1, trainingRepository.findAll().size());
    }

    @Test
    public void testFindTraineeTrainings() {
        Trainee createdTrainee = traineeRepository.create(trainee);
        Trainer createdTrainer = trainerRepository.create(trainer);
        Trainer createdTrainer2 = trainerRepository.create(buildTrainer("Kim", "Young"));
        TrainingType trainingType1 = new TrainingType();
        trainingType1.setId(1L);
        TrainingType trainingType2 = new TrainingType();
        trainingType2.setId(2L);

        Training training1 = buildTraining(createdTrainee, createdTrainer, trainingType1);
        Training training2 = buildTraining(createdTrainee, createdTrainer2, trainingType2);

        trainingRepository.create(training1);
        trainingRepository.create(training2);

        List<Training> traineeTrainings = trainingRepository.findTraineeTrainings(
                "Adam.Simpson", null, null, null, null);

        assertEquals(2, traineeTrainings.size());
        assertEquals(traineeTrainings.get(0).getTrainee().getId(), createdTrainee.getId());
        assertEquals(traineeTrainings.get(1).getTrainee().getId(), createdTrainee.getId());
    }

    @Test
    public void testFindTrainerTrainings() {
        Trainee createdTrainee1 = traineeRepository.create(trainee);
        Trainee createdTrainee2 = traineeRepository.create(buildTrainee("Kim", "Young"));
        Trainer createdTrainer = trainerRepository.create(trainer);
        TrainingType trainingType1 = new TrainingType();
        trainingType1.setId(1L);
        TrainingType trainingType2 = new TrainingType();
        trainingType2.setId(2L);

        Training training1 = buildTraining(createdTrainee1, createdTrainer, trainingType1);
        Training training2 = buildTraining(createdTrainee2, createdTrainer, trainingType2);

        trainingRepository.create(training1);
        trainingRepository.create(training2);

        List<Training> trainerTrainings = trainingRepository.findTrainerTrainings(
                "Will.Salas", null, null, null);

        assertEquals(2, trainerTrainings.size());
        assertEquals(trainerTrainings.get(0).getTrainer().getId(), createdTrainer.getId());
        assertEquals(trainerTrainings.get(1).getTrainer().getId(), createdTrainer.getId());
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

    private static TrainingType buildTrainingType() {
        return TrainingType.builder()
                .id(1L)
                .trainingTypeName("Cardio")
                .build();
    }

    private static Training buildTraining(Trainee trainee, Trainer trainer, TrainingType trainingType) {
        return Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .name("Hard Cardio")
                .trainingType(trainingType)
                .date(LocalDate.now().plusDays(2))
                .duration(90)
                .build();
    }
}