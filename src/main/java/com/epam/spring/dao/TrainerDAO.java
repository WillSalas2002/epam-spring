package com.epam.spring.dao;

import com.epam.spring.storage.InMemoryStorage;
import com.epam.spring.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class TrainerDAO implements BaseOperationsDAO<Trainer, UUID>, ExtendedOperationsDAO<Trainer> {

    private final InMemoryStorage inMemoryStorage;

    @Autowired
    public TrainerDAO(InMemoryStorage inMemoryStorage) {
        this.inMemoryStorage = inMemoryStorage;
    }

    @Override
    public Trainer create(Trainer trainer) {
        return inMemoryStorage.createTrainer(trainer);
    }

    @Override
    public List<Trainer> findAll() {
        return inMemoryStorage.findAllTrainers();
    }

    @Override
    public Trainer findById(UUID uuid) {
        return inMemoryStorage.findTrainerById(uuid);
    }

    @Override
    public Trainer update(Trainer trainer) {
        return inMemoryStorage.updateTrainer(trainer);
    }

    @Override
    public void delete(Trainer trainer) {
        inMemoryStorage.deleteTrainer(trainer);
    }
}
