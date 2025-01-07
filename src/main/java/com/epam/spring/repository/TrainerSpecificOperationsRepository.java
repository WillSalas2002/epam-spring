package com.epam.spring.repository;

import com.epam.spring.model.Trainer;

import java.util.List;

public interface TrainerSpecificOperationsRepository {

    List<Trainer> findTrainersByTraineeUsername(String username);
}
