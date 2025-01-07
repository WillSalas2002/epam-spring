package com.epam.spring.service;

import com.epam.spring.model.Trainer;

import java.util.List;

public interface TrainerSpecificOperationsService {

    List<Trainer> findTrainersByTraineeUsername(String username);
}
