package com.epam.spring.service;

import com.epam.spring.model.Trainee;
import com.epam.spring.repository.TraineeRepository;
import com.epam.spring.util.PasswordGenerator;
import com.epam.spring.util.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class TraineeService implements BaseOperationsService<Trainee>, ExtendedOperationsService<Trainee> {

    private final UsernameGenerator usernameGenerator;
    private final TraineeRepository traineeRepository;
    private final PasswordGenerator passwordGenerator;

    @Override
    public Trainee create(Trainee trainee) {
        String uniqueUsername = usernameGenerator.generateUniqueUsername(trainee.getFirstName(), trainee.getLastName());
        String password = passwordGenerator.generatePassword();
        trainee.setUsername(uniqueUsername);
        trainee.setPassword(password);
        return traineeRepository.create(trainee);
    }

    @Override
    public List<Trainee> findAll() {
        return traineeRepository.findAll();
    }

    @Override
    public Trainee findById(Long id) {
        return traineeRepository.findById(id);
    }

    @Override
    public Trainee update(Trainee updatedTrainee) {
        Long id = updatedTrainee.getId();
        Trainee trainee = traineeRepository.findById(id);
        if (trainee == null) {
            throw new NoSuchElementException("Trainee with id " + id + " not found");
        }

        if (isNameChanged(trainee, updatedTrainee)) {
            String uniqueUsername = usernameGenerator.generateUniqueUsername(updatedTrainee.getFirstName(), updatedTrainee.getLastName());
            updatedTrainee.setUsername(uniqueUsername);
        }
        return traineeRepository.update(updatedTrainee);
    }

    @Override
    public Trainee findByUsername(String username) {
        return traineeRepository.findByUsername(username);
    }

    @Override
    public boolean authorize(String username, String password) {
        Trainee trainee = traineeRepository.findByUsername(username);
        if (trainee == null) {
            return false;
        }
        return Objects.equals(trainee.getPassword(), password);
    }

    @Override
    public void activate(Trainee trainee) {
        trainee.setActive(!trainee.isActive());
        traineeRepository.update(trainee);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainee trainee = traineeRepository.findByUsername(username);

        if (trainee == null) {
            throw new RuntimeException("Incorrect username");
        }

        if (!Objects.equals(trainee.getPassword(), oldPassword)) {
            throw new RuntimeException("Incorrect password");
        }
        trainee.setPassword(newPassword);
        traineeRepository.update(trainee);
    }

    @Override
    public void delete(Trainee trainee) {
        traineeRepository.delete(trainee);
    }

    public void deleteByUsername(String username) {
        traineeRepository.deleteByUsername(username);
    }

    private boolean isNameChanged(Trainee trainee, Trainee updatedTrainee) {
        return !Objects.equals(trainee.getFirstName(), updatedTrainee.getFirstName()) ||
                !Objects.equals(trainee.getLastName(), updatedTrainee.getLastName());
    }
}
