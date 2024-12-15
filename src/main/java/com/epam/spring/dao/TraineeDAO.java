package com.epam.spring.dao;

import com.epam.spring.model.Trainee;
import com.epam.spring.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class TraineeDAO implements BaseOperationsDAO<Trainee>, ExtendedOperationsDAO<Trainee> {

    private final InMemoryStorage inMemoryStorage;

    @Autowired
    public TraineeDAO(InMemoryStorage inMemoryStorage) {
        this.inMemoryStorage = inMemoryStorage;
    }

    @Override
    public Trainee create(Trainee trainee) {
        Map<UUID, Trainee> traineeStorage = inMemoryStorage.getTraineeStorage();
        return traineeStorage.put(trainee.getUuid(), trainee);
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
