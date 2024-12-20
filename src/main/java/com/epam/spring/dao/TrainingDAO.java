package com.epam.spring.dao;

import com.epam.spring.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class TrainingDAO implements BaseOperationsDAO<Training> {

    private Map<UUID, Training> trainingStorage;

    @Autowired
    public void setTrainingStorage(Map<UUID, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Override
    public Training create(Training training) {
        UUID uuid = UUID.randomUUID();
        training.setUuid(uuid);
        trainingStorage.put(uuid, training);
        return training;
    }

    @Override
    public List<Training> findAll() {
        return trainingStorage.values().stream().toList();
    }

    @Override
    public Training findById(UUID uuid) {
        return trainingStorage.get(uuid);
    }
}
