package com.epam.spring.service;

import com.epam.spring.dao.TrainerDAO;
import com.epam.spring.model.Trainer;
import com.epam.spring.util.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TrainerService implements BaseOperationsService<Trainer, UUID>, ExtendedOperationsService<Trainer> {

    private final TrainerDAO trainerDAO;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public TrainerService(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    @Override
    public Trainer create(Trainer trainer) {
        trainer.setUsername(trainer.getFirstName() + "." + trainer.getLastName());
        trainer.setPassword(passwordGenerator.generatePassword());
        return trainerDAO.create(trainer);
    }

    @Override
    public List<Trainer> findAll() {
        return trainerDAO.findAll();
    }

    @Override
    public Trainer findById(UUID uuid) {
        return trainerDAO.findById(uuid);
    }

    @Override
    public Trainer update(Trainer trainer) {
        return trainerDAO.update(trainer);
    }

    @Override
    public void delete(Trainer trainer) {
        trainerDAO.delete(trainer);
    }
}
