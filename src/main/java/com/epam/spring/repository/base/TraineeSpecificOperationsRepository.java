package com.epam.spring.repository.base;

import com.epam.spring.model.Trainee;

public interface TraineeSpecificOperationsRepository extends ExtendedOperationsRepository<Trainee> {

    void deleteByUsername(String username);
}
