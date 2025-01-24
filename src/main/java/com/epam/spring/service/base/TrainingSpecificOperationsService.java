package com.epam.spring.service.base;

import com.epam.spring.dto.request.training.CreateTrainingRequestDTO;
import com.epam.spring.dto.request.training.FetchTraineeTrainingsRequestDTO;
import com.epam.spring.dto.request.training.FetchTrainerTrainingsRequestDTO;
import com.epam.spring.dto.response.training.FetchUserTrainingsResponseDTO;

import java.util.List;

public interface TrainingSpecificOperationsService {

    List<FetchUserTrainingsResponseDTO> findTraineeTrainings(String username, FetchTraineeTrainingsRequestDTO fetchTraineeTrainingsRequest);
    List<FetchUserTrainingsResponseDTO> findTrainerTrainings(String username, FetchTrainerTrainingsRequestDTO fetchTrainerTrainingsRequest);
    void create(CreateTrainingRequestDTO createTrainingRequest);
}
