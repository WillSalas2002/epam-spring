package com.epam.spring.repository;

import com.epam.spring.model.Trainee;

public interface TraineeSpecificOperationsRepository extends ExtendedOperationsRepository<Trainee> {

    void deleteByUsername(String username);
}
