package com.epam.spring.dao;

import com.epam.spring.storage.InMemoryStorage;
import com.epam.spring.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class TrainingDAO implements BaseDAO<Training, UUID> {

    private final InMemoryStorage inMemoryStorage;

    @Autowired
    public TrainingDAO(InMemoryStorage inMemoryStorage) {
        this.inMemoryStorage = inMemoryStorage;
    }

    @Override
    public Training create(Training training) {
        return inMemoryStorage.createTraining(training);
    }

    @Override
    public List<Training> findAll() {
        return inMemoryStorage.findAllTrainings();
    }

    @Override
    public Training findById(UUID uuid) {
        return inMemoryStorage.findTrainingById(uuid);
    }
}
