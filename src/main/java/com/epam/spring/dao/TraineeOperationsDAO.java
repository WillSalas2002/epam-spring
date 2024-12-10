package com.epam.spring.dao;

import com.epam.spring.storage.InMemoryStorage;
import com.epam.spring.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class TraineeOperationsDAO implements BaseOperationsDAO<Trainee, UUID>, ExtendedOperationsDAO<Trainee> {

    private final InMemoryStorage inMemoryStorage;

    @Autowired
    public TraineeOperationsDAO(InMemoryStorage inMemoryStorage) {
        this.inMemoryStorage = inMemoryStorage;
    }

    @Override
    public Trainee create(Trainee trainee) {
        return inMemoryStorage.createTrainee(trainee);
    }

    @Override
    public List<Trainee> findAll() {
        return inMemoryStorage.findAllTrainees();
    }

    @Override
    public Trainee findById(UUID uuid) {
        return inMemoryStorage.findTraineeById(uuid);
    }

    @Override
    public Trainee update(Trainee trainee) {
        return inMemoryStorage.updateTrainee(trainee);
    }

    @Override
    public void delete(Trainee trainee) {
        inMemoryStorage.deleteTrainee(trainee);
    }
}
