package com.epam.spring.service;

import com.epam.spring.dao.TrainingRepository;
import com.epam.spring.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingService implements BaseOperationsService<Training> {

    private final TrainingRepository trainingRepository;

    @Autowired
    public TrainingService(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @Override
    public List<Training> findAll() {
        return trainingRepository.findAll();
    }

    @Override
    public Training findById(Long id) {
        return trainingRepository.findById(id);
    }

    @Override
    public Training create(Training training) {
        return trainingRepository.create(training);
    }
}
