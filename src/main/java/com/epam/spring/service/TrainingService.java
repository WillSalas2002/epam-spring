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
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@AllArgsConstructor
@Service
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainerService trainerService;
    private final TraineeService traineeService;

//    @Override
    public List<Training> findAll() {
        return trainingRepository.findAll();
    }

//    @Override
    public Training findById(Long id) {
        Optional<Training> trainingOptional = trainingRepository.findById(id);
        if (trainingOptional.isEmpty()) {
            throw new RuntimeException("Training with id " + id + " not found");
        }
        return trainingOptional.get();
    }

//    @Override
    public Training create(Training training) {
        Trainee trainee = null;
//        findEntityOrThrow(
//                training.getTrainee().getUser().getUsername(),
//                traineeService::findByUsername
//        );

        Trainer trainer = null;
//                findEntityOrThrow(
//                training.getTrainer().getUser().getUsername(),
//                trainerService::findByUsername
//        );

        training.setTrainee(trainee);
        training.setTrainer(trainer);
        return trainingRepository.create(training);
    }

//    @Override
    public List<Training> findTraineeTrainings(String traineeUsername,
                                               LocalDate fromDate,
                                               LocalDate toDate,
                                               String trainerName,
                                               String trainingType) {
        return trainingRepository.findTraineeTrainings(traineeUsername, fromDate, toDate, trainerName, trainingType);
    }

//    @Override
    public List<Training> findTrainerTrainings(String trainerUsername,
                                               LocalDate fromDate,
                                               LocalDate toDate,
                                               String traineeName,
                                               String trainingType) {
        return trainingRepository.findTrainerTrainings(trainerUsername, fromDate, toDate, traineeName, trainingType);
    }

    private <T> T findEntityOrThrow(String username, Function<String, T> findByUsername) {
        if (username == null) {
            throw new RuntimeException("User not found");
        } else {
            return findByUsername.apply(username);
        }
    }
}
