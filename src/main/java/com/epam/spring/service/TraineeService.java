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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class TraineeService implements BaseOperationsService<Trainee>, ExtendedOperationsService<Trainee>, TraineeSpecificOperationsService {

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
        Optional<Trainee> traineeOptional = traineeRepository.findById(id);
        if (traineeOptional.isEmpty()) {
            throw new RuntimeException("Trainee with id " + id + " not found");
        }
        return traineeOptional.get();
    }

    @Override
    public Trainee update(Trainee updatedTrainee) {
        Long id = updatedTrainee.getId();
        Optional<Trainee> traineeOptional = traineeRepository.findById(id);
        if (traineeOptional.isEmpty()) {
            throw new NoSuchElementException("Trainee with id " + id + " not found");
        }

        if (isNameChanged(traineeOptional.get(), updatedTrainee)) {
            String uniqueUsername = usernameGenerator.generateUniqueUsername(updatedTrainee.getFirstName(), updatedTrainee.getLastName());
            updatedTrainee.setUsername(uniqueUsername);
        }
        return traineeRepository.update(updatedTrainee);
    }

    @Override
    public Trainee findByUsername(String username) {
        Optional<Trainee> traineeOptional = traineeRepository.findByUsername(username);
        if (traineeOptional.isEmpty()) {
            throw new RuntimeException("Trainee with username " + username + " not found");
        }
        return traineeOptional.get();
    }

    @Override
    public boolean authorize(String username, String password) {
        Optional<Trainee> traineeOptional = traineeRepository.findByUsername(username);

        return traineeOptional.filter(trainee -> Objects.equals(trainee.getPassword(), password)).isPresent();
    }

    @Override
    public void activate(Trainee trainee) {
        trainee.setActive(!trainee.isActive());
        traineeRepository.update(trainee);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        Optional<Trainee> traineeOptional = traineeRepository.findByUsername(username);

        if (traineeOptional.isEmpty()) {
            throw new RuntimeException("Incorrect username");
        }

        Trainee trainee = traineeOptional.get();

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

    @Override
    public void deleteByUsername(String username) {
        traineeRepository.deleteByUsername(username);
    }

    private boolean isNameChanged(Trainee trainee, Trainee updatedTrainee) {
        return !Objects.equals(trainee.getFirstName(), updatedTrainee.getFirstName()) ||
                !Objects.equals(trainee.getLastName(), updatedTrainee.getLastName());
    }
}
