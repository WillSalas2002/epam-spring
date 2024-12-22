package com.epam.spring.service;

import com.epam.spring.dao.TraineeRepository;
import com.epam.spring.model.Trainee;
import com.epam.spring.util.PasswordGenerator;
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

    private final UserService userService;
    private final TraineeRepository traineeRepository;
    private final PasswordGenerator passwordGenerator;

    @Override
    public Trainee create(Trainee trainee) {
        String uniqueUsername = userService.generateUniqueUsername(trainee.getFirstName(), trainee.getLastName());
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
        // TODO: see if updated or adds and think about password change as well!
        if (isNameChanged(trainee, updatedTrainee)) {
            String uniqueUsername = userService.generateUniqueUsername(updatedTrainee.getFirstName(), updatedTrainee.getLastName());
            updatedTrainee.setUsername(uniqueUsername);
        }
        return traineeRepository.update(updatedTrainee);
    }

    @Override
    public void delete(Trainee trainee) {
        traineeRepository.delete(trainee);
    }

    private boolean isNameChanged(Trainee trainee, Trainee updatedTrainee) {
        return !Objects.equals(trainee.getFirstName(), updatedTrainee.getFirstName()) ||
                !Objects.equals(trainee.getLastName(), updatedTrainee.getLastName());
    }
}
