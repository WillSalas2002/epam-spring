package com.epam.spring.repository;

import com.epam.spring.model.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainingSpecificOperationsRepository {

    List<Training> findTraineeTrainings(String traineeUsername,
                                        LocalDate fromDate,
                                        LocalDate toDate,
                                        String trainerName,
                                        String trainingType);

    List<Training> findTrainerTrainings(String trainerUsername,
                                        LocalDate fromDate,
                                        LocalDate toDate,
                                        String traineeName,
                                        String trainingType);
}
