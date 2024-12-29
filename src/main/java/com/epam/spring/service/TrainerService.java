package com.epam.spring.service;

import com.epam.spring.repository.TrainerRepository;
import com.epam.spring.model.Trainer;
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
public class TrainerService implements BaseOperationsService<Trainer>, ExtendedOperationsService<Trainer> {

    private final UserService userService;
    private final TrainerRepository trainerRepository;
    private final PasswordGenerator passwordGenerator;

    @Override
    public Trainer create(Trainer trainer) {
        String uniqueUsername = userService.generateUniqueUsername(trainer.getFirstName(), trainer.getLastName());
        String password = passwordGenerator.generatePassword();
        trainer.setUsername(uniqueUsername);
        trainer.setPassword(password);
        return trainerRepository.create(trainer);
    }

    @Override
    public List<Trainer> findAll() {
        return trainerRepository.findAll();
    }

    @Override
    public Trainer findById(Long id) {
        return trainerRepository.findById(id);
    }

    @Override
    public Trainer findByUsername(String username) {
        return trainerRepository.findByUsername(username);
    }

    @Override
    public boolean authorize(String username, String password) {
        Trainer trainer = trainerRepository.findByUsername(username);
        if (trainer == null) {
            return false;
        }
        return Objects.equals(trainer.getPassword(), password);
    }

    @Override
    public Trainer update(Trainer updatedTrainer) {
        Long id = updatedTrainer.getId();
        Trainer trainer = trainerRepository.findById(id);
        if (trainer == null) {
            throw new NoSuchElementException("Trainee with id " + id + " not found");
        }
        if (isNameChanged(trainer, updatedTrainer)) {
            String uniqueUsername = userService.generateUniqueUsername(updatedTrainer.getFirstName(), updatedTrainer.getLastName());
            updatedTrainer.setUsername(uniqueUsername);
        }
        return trainerRepository.update(updatedTrainer);
    }

    @Override
    public void activate(Trainer trainer) {
        trainer.setActive(!trainer.isActive());
        trainerRepository.update(trainer);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainer trainer = trainerRepository.findByUsername(username);
        if (!Objects.equals(trainer.getPassword(), oldPassword)) {
            throw new RuntimeException("Incorrect password");
        }
        trainer.setPassword(newPassword);
        trainerRepository.update(trainer);
    }

    @Override
    public void delete(Trainer trainer) {
        trainerRepository.delete(trainer);
    }

    private boolean isNameChanged(Trainer trainer, Trainer updatedTrainer) {
        return !Objects.equals(trainer.getFirstName(), updatedTrainer.getFirstName()) ||
                !Objects.equals(trainer.getLastName(), updatedTrainer.getLastName());
    }
}
