package com.epam.spring.repository.base;

import com.epam.spring.model.TrainingType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingTypeOperationsRepository {

    List<TrainingType> findAll();
    Optional<TrainingType> findById(Long id);
}
