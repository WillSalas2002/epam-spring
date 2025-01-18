package com.epam.spring.repository;

import com.epam.spring.model.Training;

import java.time.LocalDateTime;
import java.util.List;

public interface TrainingSpecificOperationsRepository {

    Training create(Training training);
    List<Training> findTraineeTrainings(String traineeUsername,
                                        LocalDateTime fromDate,
                                        LocalDateTime toDate,
                                        String trainerName,
                                        String trainingType);
    List<Training> findTrainerTrainings(String trainerUsername,
                                        LocalDateTime fromDate,
                                        LocalDateTime toDate,
                                        String traineeName);
}
