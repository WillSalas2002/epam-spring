package com.epam.spring.repository;

import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import com.epam.spring.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class TrainingRepositoryTest {

    @Autowired
    private TrainingRepository trainingRepository;
    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private TraineeRepository traineeRepository;

    private Trainer trainer;
    private Trainee trainee;
    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @BeforeEach
    void setUp() {
        TrainingType trainingType = trainingTypeRepository.findById(1L).get();
        trainer = buildTrainer("Will", "Salas");
        trainer.setSpecialization(trainingType);
        trainee = buildTrainee("Adam", "Simpson");
    }

    @Test
    public void testCreateTraining() {
        Trainee createdTrainee = traineeRepository.save(trainee);
        Trainer createdTrainer = trainerRepository.save(trainer);
        TrainingType trainingType = createdTrainer.getSpecialization();

        Training training = buildTraining(createdTrainee, createdTrainer, trainingType, LocalDate.now());

        Training createdTraining = trainingRepository.save(training);
        Optional<Training> trainingByIdOptional = trainingRepository.findById(createdTraining.getId());

        assertNotNull(createdTraining);
        assertTrue(trainingByIdOptional.isPresent());
        assertEquals(90, trainingByIdOptional.get().getDuration());
        assertEquals(1, trainingRepository.findAll().size());
    }

    @Test
    public void testFindTraineeTrainings() {
        Trainee createdTrainee = traineeRepository.save(trainee);
        Trainer createdTrainer = trainerRepository.save(trainer);
        Trainer trainer2 = buildTrainer("Kim", "Young");
        trainer2.setSpecialization(trainingTypeRepository.findById(2L).get());
        Trainer createdTrainer2 = trainerRepository.save(trainer2);
        TrainingType trainingType1 = new TrainingType();
        trainingType1.setId(1L);
        TrainingType trainingType2 = new TrainingType();
        trainingType2.setId(2L);

        Training training1 = buildTraining(createdTrainee, createdTrainer, trainingType1, LocalDate.now());
        Training training2 = buildTraining(createdTrainee, createdTrainer2, trainingType2, LocalDate.now().minusDays(2));

        trainingRepository.save(training1);
        trainingRepository.save(training2);

        List<Training> traineeTrainings = trainingRepository.findTraineeTrainings(
                "Adam.Simpson", null, null, null, null);

        assertEquals(2, traineeTrainings.size());
        assertEquals(traineeTrainings.get(0).getTrainee().getId(), createdTrainee.getId());
        assertEquals(traineeTrainings.get(1).getTrainee().getId(), createdTrainee.getId());
    }

    @Test
    public void testFindTrainerTrainings() {
        Trainee createdTrainee1 = traineeRepository.save(trainee);
        Trainee createdTrainee2 = traineeRepository.save(buildTrainee("Kim", "Young"));
        Trainer createdTrainer = trainerRepository.save(trainer);
        TrainingType trainingType1 = new TrainingType();
        trainingType1.setId(1L);
        TrainingType trainingType2 = new TrainingType();
        trainingType2.setId(2L);

        Training training1 = buildTraining(createdTrainee1, createdTrainer, trainingType1, LocalDate.now());
        Training training2 = buildTraining(createdTrainee2, createdTrainer, trainingType2, LocalDate.now().minusDays(2));

        trainingRepository.save(training1);
        trainingRepository.save(training2);

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
                .build();
    }

    private static Training buildTraining(Trainee trainee, Trainer trainer, TrainingType trainingType, LocalDate date) {
        return Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .name("Hard Cardio")
                .trainingType(trainingType)
                .date(date)
                .duration(90)
                .build();
    }
}