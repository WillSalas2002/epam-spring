package com.epam.spring.service;

import com.epam.spring.dto.request.trainee.UpdateTraineeTrainerRequestDTO;
import com.epam.spring.dto.response.trainer.TrainerResponseDTO;

public interface TraineeSpecificOperationsService {

    void deleteByUsername(String username);
    TrainerResponseDTO updateTraineeTrainerList(UpdateTraineeTrainerRequestDTO request);
}
