package com.epam.spring.repository.base;

import com.epam.spring.model.Trainer;

import java.util.List;

public interface TrainerSpecificOperationsRepository extends ExtendedOperationsRepository<Trainer> {

    List<Trainer> findUnassignedTrainersByTraineeUsername(String username);
}
