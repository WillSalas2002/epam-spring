package com.epam.spring.service;

import com.epam.spring.dto.request.training.CreateTrainingRequestDTO;
import com.epam.spring.dto.request.training.FetchTraineeTrainingsRequestDTO;
import com.epam.spring.dto.request.training.FetchTrainerTrainingsRequestDTO;
import com.epam.spring.dto.response.training.FetchUserTrainingsResponseDTO;

import java.util.List;

public interface TrainingSpecificOperationsService {

    List<FetchUserTrainingsResponseDTO> findTraineeTrainings(FetchTraineeTrainingsRequestDTO fetchTraineeTrainingsRequest);
    List<FetchUserTrainingsResponseDTO> findTrainerTrainings(FetchTrainerTrainingsRequestDTO fetchTrainerTrainingsRequest);
    void create(CreateTrainingRequestDTO createTrainingRequest);
}
