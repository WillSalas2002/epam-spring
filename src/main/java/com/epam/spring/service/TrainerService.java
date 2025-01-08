package com.epam.spring.service;

import com.epam.spring.model.Trainer;
import com.epam.spring.repository.TrainerRepository;
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
public class TrainerService implements BaseOperationsService<Trainer>, ExtendedOperationsService<Trainer>, TrainerSpecificOperationsService {

    private final UsernameGenerator usernameGenerator;
    private final TrainerRepository trainerRepository;
    private final PasswordGenerator passwordGenerator;

    @Override
    public Trainer create(Trainer trainer) {
        String uniqueUsername = usernameGenerator.generateUniqueUsername(trainer.getFirstName(), trainer.getLastName());
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
        Optional<Trainer> trainerOptional = trainerRepository.findById(id);
        if (trainerOptional.isEmpty()) {
            throw new RuntimeException("Trainer with id " + id + " not found");
        }
        return trainerOptional.get();
    }

    @Override
    public Trainer findByUsername(String username) {
        Optional<Trainer> trainerOptional = trainerRepository.findByUsername(username);
        if (trainerOptional.isEmpty()) {
            throw new RuntimeException("Trainer with username " + username + " not found");
        }
        return trainerOptional.get();
    }

    @Override
    public boolean authenticate(String username, String password) {
        Optional<Trainer> trainerOptional = trainerRepository.findByUsername(username);

        return trainerOptional.filter(trainer -> Objects.equals(trainer.getPassword(), password)).isPresent();
    }

    @Override
    public Trainer update(Trainer updatedTrainer) {
        Long id = updatedTrainer.getId();
        Optional<Trainer> trainerOptional = trainerRepository.findById(id);
        if (trainerOptional.isEmpty()) {
            throw new NoSuchElementException("Trainee with id " + id + " not found");
        }
        if (isNameChanged(trainerOptional.get(), updatedTrainer)) {
            String uniqueUsername = usernameGenerator.generateUniqueUsername(updatedTrainer.getFirstName(), updatedTrainer.getLastName());
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
        Optional<Trainer> trainerOptional = trainerRepository.findByUsername(username);
        if (trainerOptional.isEmpty()) {
            throw new RuntimeException("Incorrect username");
        }
        Trainer trainer = trainerOptional.get();
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

    @Override
    public List<Trainer> findTrainersByTraineeUsername(String username){
        return trainerRepository.findTrainersByTraineeUsername(username);
    }

    private boolean isNameChanged(Trainer trainer, Trainer updatedTrainer) {
        return !Objects.equals(trainer.getFirstName(), updatedTrainer.getFirstName()) ||
                !Objects.equals(trainer.getLastName(), updatedTrainer.getLastName());
    }
}
