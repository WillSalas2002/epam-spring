package com.epam.spring.service;

import com.epam.spring.dto.response.trainer.TrainerResponseDTO;

import java.util.List;

public interface TrainerSpecificOperationsService {

    List<TrainerResponseDTO> findUnassignedTrainersByTraineeUsername(String username);
}
