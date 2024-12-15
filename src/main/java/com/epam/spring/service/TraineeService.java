package com.epam.spring.service;

import com.epam.spring.dao.TraineeDAO;
import com.epam.spring.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TraineeService implements BaseOperationsService<Trainee>, ExtendedOperationsService<Trainee> {

    private final TraineeDAO traineeDAO;

    @Autowired
    public TraineeService(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    @Override
    public Trainee create(Trainee trainee) {
        return traineeDAO.create(trainee);
    }

    @Override
    public List<Trainee> findAll() {
        return traineeDAO.findAll();
    }

    @Override
    public Trainee findById(UUID uuid) {
        return traineeDAO.findById(uuid);
    }

    @Override
    public Trainee update(Trainee trainee) {
        return traineeDAO.update(trainee);
    }

    @Override
    public void delete(Trainee trainee) {
        traineeDAO.delete(trainee);
    }
}
