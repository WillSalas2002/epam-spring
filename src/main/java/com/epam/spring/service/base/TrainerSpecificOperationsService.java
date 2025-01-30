package com.epam.spring.service.base;

import com.epam.spring.dto.request.trainer.CreateTrainerRequestDTO;
import com.epam.spring.dto.request.trainer.UpdateTrainerRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainer.FetchTrainerResponseDTO;
import com.epam.spring.dto.response.trainer.TrainerResponseDTO;
import com.epam.spring.dto.response.trainer.UpdateTrainerResponseDTO;

import java.util.List;

public interface TrainerSpecificOperationsService extends BaseUserOperationsService<CreateTrainerRequestDTO, UserCredentialsResponseDTO, FetchTrainerResponseDTO, UpdateTrainerRequestDTO, UpdateTrainerResponseDTO> {

    List<TrainerResponseDTO> findUnassignedTrainersByTraineeUsername(String username);
}
