package com.epam.spring.service;

import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.repository.TrainingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

@Slf4j
@AllArgsConstructor
@Service
public class TrainingService implements BaseOperationsService<Training>, TrainingSpecificOperationsService {

    private final TrainingRepository trainingRepository;
    private final TrainerService trainerService;
    private final TraineeService traineeService;

    @Override
    public List<Training> findAll() {
        return trainingRepository.findAll();
    }

    @Override
    public Training findById(Long id) {
        return trainingRepository.findById(id);
    }

    @Override
    public Training create(Training training) {
        Trainee trainee = getOrCreateEntity(
                training.getTrainee().getUsername(),
                traineeService::findByUsername
        );

        Trainer trainer = getOrCreateEntity(
                training.getTrainer().getUsername(),
                trainerService::findByUsername
        );

        training.setTrainee(trainee);
        training.setTrainer(trainer);
        return trainingRepository.create(training);
    }

    @Override
    public List<Training> findTraineeTrainings(String traineeUsername,
                                               LocalDate fromDate,
                                               LocalDate toDate,
                                               String trainerName,
                                               String trainingType) {
        return trainingRepository.findTraineeTrainings(traineeUsername, fromDate, toDate, trainerName, trainingType);
    }

    @Override
    public List<Training> findTrainerTrainings(String trainerUsername,
                                               LocalDate fromDate,
                                               LocalDate toDate,
                                               String traineeName,
                                               String trainingType) {
        return trainingRepository.findTrainerTrainings(trainerUsername, fromDate, toDate, traineeName, trainingType);
    }

    private <T> T getOrCreateEntity(String username, Function<String, T> findByUsername) {
        if (username == null) {
            throw new RuntimeException("User not found");
        } else {
            return findByUsername.apply(username);
        }
    }
}
