package com.epam.spring.repository;

import com.epam.spring.model.TrainingType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingTypeOperationsRepository {

    List<TrainingType> findAll();
}
