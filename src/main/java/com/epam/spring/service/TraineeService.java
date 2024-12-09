package com.epam.spring.service;

import com.epam.spring.dao.TraineeDAO;
import com.epam.spring.model.Trainee;
import com.epam.spring.util.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TraineeService implements BaseService<Trainee, UUID> {

    private final TraineeDAO traineeDAO;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public TraineeService(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    @Override
    public Trainee create(Trainee trainee) {
        trainee.setUsername(trainee.getFirstName() + "." + trainee.getLastName());
        trainee.setPassword(passwordGenerator.generatePassword());
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

    public Trainee update(Trainee trainee) {
        return traineeDAO.update(trainee);
    }

    public void delete(Trainee trainee) {
        traineeDAO.delete(trainee);
    }
}
