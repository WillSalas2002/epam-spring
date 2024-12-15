package com.epam.spring.dao;

import com.epam.spring.model.Trainee;
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
public class TraineeDAO implements BaseOperationsDAO<Trainee>, ExtendedOperationsDAO<Trainee> {

    private Map<UUID, Trainee> traineeStorage;
    private Set<String> usernameStorage;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public void setTraineeStorage(Map<UUID, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
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
    public Trainee create(Trainee trainee) {
        UUID uuid = UUID.randomUUID();
        String uniqueUsername = usernameGenerator.generateUniqueUsername(trainee.getFirstName(), trainee.getLastName(), usernameStorage);
        String password = passwordGenerator.generatePassword();

        trainee.setUuid(uuid);
        trainee.setUsername(uniqueUsername);
        trainee.setPassword(password);

        traineeStorage.put(uuid, trainee);
        return trainee;
    }

    @Override
    public List<Trainee> findAll() {
        return traineeStorage.values().stream().toList();
    }

    @Override
    public Trainee findById(UUID uuid) {
        return traineeStorage.get(uuid);
    }

    @Override
    public Trainee update(Trainee updatedTrainee) {
        UUID uuid = updatedTrainee.getUuid();
        Trainee trainee = traineeStorage.get(uuid);
        if (trainee == null) {
            throw new NoSuchElementException("Trainee with id " + uuid + " not found");
        }
        String uniqueUsername = usernameGenerator.generateUniqueUsername(updatedTrainee.getFirstName(), updatedTrainee.getLastName(), usernameStorage);
        updatedTrainee.setUsername(uniqueUsername);
        traineeStorage.put(uuid, updatedTrainee);
        return updatedTrainee;
    }

    @Override
    public void delete(Trainee trainee) {
        traineeStorage.remove(trainee.getUuid());
    }
}
