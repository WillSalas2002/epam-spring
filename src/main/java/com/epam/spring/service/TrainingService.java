package com.epam.spring.service;

import com.epam.spring.dao.TrainingDAO;
import com.epam.spring.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TrainingService implements BaseOperationsService<Training> {

    private final TrainingDAO trainingDAO;

    @Autowired
    public TrainingService(TrainingDAO trainingDAO) {
        this.trainingDAO = trainingDAO;
    }

    @Override
    public List<Training> findAll() {
        return trainingDAO.findAll();
    }

    @Override
    public Training findById(UUID uuid) {
        return trainingDAO.findById(uuid);
    }

    @Override
    public Training create(Training training) {
        return trainingDAO.create(training);
    }
}
