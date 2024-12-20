package com.epam.spring.dao;

import com.epam.spring.model.Trainer;
import com.epam.spring.util.PasswordGenerator;
import com.epam.spring.util.UsernameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Repository
public class TrainerDAO implements BaseOperationsDAO<Trainer>, ExtendedOperationsDAO<Trainer> {

    private Map<UUID, Trainer> trainerStorage;
    private Set<String> usernameStorage;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public void setTrainerStorage(Map<UUID, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Autowired
    public void setUsernameStorage(Set<String> usernameStorage) {
        this.usernameStorage = usernameStorage;
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    @Override
    public Trainer create(Trainer trainer) {
        UUID uuid = UUID.randomUUID();
        String uniqueUsername = usernameGenerator.generateUniqueUsername(trainer.getFirstName(), trainer.getLastName(), usernameStorage);
        String password = passwordGenerator.generatePassword();

        trainer.setUuid(uuid);
        trainer.setUsername(uniqueUsername);
        trainer.setPassword(password);

        trainerStorage.put(uuid, trainer);
        return trainer;
    }

    @Override
    public List<Trainer> findAll() {
        return trainerStorage.values().stream().toList();
    }

    @Override
    public Trainer findById(UUID uuid) {
        return trainerStorage.get(uuid);
    }

    @Override
    public Trainer update(Trainer updatedTrainer) {
        UUID uuid = updatedTrainer.getUuid();
        Trainer trainer = trainerStorage.get(uuid);
        if (trainer == null) {
            throw new NoSuchElementException("Trainee with id " + uuid + " not found");
        }
        String uniqueUsername = usernameGenerator.generateUniqueUsername(updatedTrainer.getFirstName(), updatedTrainer.getLastName(), usernameStorage);
        updatedTrainer.setUsername(uniqueUsername);
        trainerStorage.put(uuid, updatedTrainer);
        return updatedTrainer;
    }

    @Override
    public void delete(Trainer trainer) {
        trainerStorage.remove(trainer.getUuid());
    }
}
