package com.epam.spring.service;

import com.epam.spring.dto.request.trainee.CreateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.UpdateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.UpdateTraineeTrainerRequestDTO;
import com.epam.spring.dto.request.user.UserActivationRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainee.FetchTraineeResponseDTO;
import com.epam.spring.dto.response.trainee.UpdateTraineeResponseDTO;
import com.epam.spring.dto.response.trainer.TrainerResponseDTO;

public interface TraineeSpecificOperationsService extends BaseUserOperationsService<CreateTraineeRequestDTO, UserCredentialsResponseDTO, FetchTraineeResponseDTO, UpdateTraineeRequestDTO, UpdateTraineeResponseDTO, UserActivationRequestDTO> {

    void deleteByUsername(String username);
    TrainerResponseDTO updateTraineeTrainerList(String username, UpdateTraineeTrainerRequestDTO request);
}
