package com.epam.spring.service;

import com.epam.spring.model.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainingSpecificOperationsService {

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
