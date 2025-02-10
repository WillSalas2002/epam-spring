package com.epam.spring.service.impl;

import com.epam.spring.dto.request.trainee.CreateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.TrainingIdTrainerUsernamePair;
import com.epam.spring.dto.request.trainee.UpdateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.UpdateTraineeTrainerRequestDTO;
import com.epam.spring.dto.response.TrainingTypeDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainee.FetchTraineeResponseDTO;
import com.epam.spring.dto.response.trainee.UpdateTraineeResponseDTO;
import com.epam.spring.dto.response.trainer.TrainerResponseDTO;
import com.epam.spring.error.exception.ResourceNotFoundException;
import com.epam.spring.mapper.TraineeMapper;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.repository.TraineeRepository;
import com.epam.spring.repository.TrainerRepository;
import com.epam.spring.service.base.TraineeSpecificOperationsService;
import com.epam.spring.util.PasswordGenerator;
import com.epam.spring.util.TransactionContext;
import com.epam.spring.util.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TraineeService implements TraineeSpecificOperationsService {

    private final UsernameGenerator usernameGenerator;
    private final TraineeRepository traineeRepository;
    private final PasswordGenerator passwordGenerator;
    private final TrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;
    private final TraineeMapper traineeMapper;

    @Override
    public UserCredentialsResponseDTO create(CreateTraineeRequestDTO createRequest) {
        String transactionId = TransactionContext.getTransactionId();
        log.info("Transaction ID: {}, Starting trainee creation for firstName: {}, lastName: {}",
                transactionId, createRequest.getFirstName(), createRequest.getLastName());

        String uniqueUsername = usernameGenerator.generateUniqueUsername(createRequest.getFirstName(), createRequest.getLastName());
        String password = passwordGenerator.generatePassword();

        log.info("Transaction ID: {}, Generated username: {}", transactionId, uniqueUsername);
        Trainee trainee = traineeMapper.fromCreateTraineeRequestToTrainee(createRequest, uniqueUsername, passwordEncoder.encode(password));

        traineeRepository.save(trainee);
        log.info("Transaction ID: {}, Successfully saved trainee with username: {}", transactionId, uniqueUsername);

        return new UserCredentialsResponseDTO(uniqueUsername, password);
    }

    @Override
    public UpdateTraineeResponseDTO updateProfile(UpdateTraineeRequestDTO updateRequest) {
        String transactionId = TransactionContext.getTransactionId();
        String username = updateRequest.getUsername();
        log.info("Transaction ID: {}, Starting trainee update for username: {}", transactionId, username);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(username));
        traineeMapper.fromUpdateTraineeRequestToTrainee(trainee, updateRequest);

        Trainee updatedTrainee = traineeRepository.save(trainee);
        log.info("Transaction ID: {}, Successfully updated trainee with username: {}", transactionId, username);

        return traineeMapper.fromTraineeToUpdateTraineeResponse(updatedTrainee);
    }

    @Override
    @Transactional(readOnly = true)
    public FetchTraineeResponseDTO getUserProfile(String username) {
        String transactionId = TransactionContext.getTransactionId();
        log.info("Transaction ID: {}, Fetching trainee with username: {}", transactionId, username);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(username));
        log.info("Transaction ID: {}, Successfully fetched trainee with username: {}", transactionId, username);

        return traineeMapper.fromTraineeToFetchTraineeResponse(trainee);
    }

    @Override
    public void deleteByUsername(String username) {
        log.info("Transaction ID: {}, Deleting trainee with username: {}",
                TransactionContext.getTransactionId(), username);
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(username));
        traineeRepository.delete(trainee);
    }

    @Override
    public List<TrainerResponseDTO> updateTraineeTrainerList(UpdateTraineeTrainerRequestDTO updateTraineeTrainerRequest) {
        String transactionId = TransactionContext.getTransactionId();
        String traineeUsername = updateTraineeTrainerRequest.getTraineeUsername();
        log.info("Transaction ID: {}, Starting to update trainee trainer list with trainee username: {}",
                transactionId, traineeUsername);

        Trainee trainee = traineeRepository.findByUsername(traineeUsername)
                .orElseThrow(ResourceNotFoundException::new);
        List<Training> trainings = trainee.getTrainings();
        List<TrainingIdTrainerUsernamePair> trainingIdTrainerUsernamePairs = updateTraineeTrainerRequest.getTrainingIdTrainerUsernamePairs();

        for (TrainingIdTrainerUsernamePair pair : trainingIdTrainerUsernamePairs) {
            for (Training training : trainings) {
                if (pair.getTrainingId().equals(training.getId())) {
                    Trainer trainer = trainerRepository.findByUsername(pair.getTrainerUsername()).orElseThrow(NoSuchElementException::new);
                    training.setTrainer(trainer);
                    training.setTrainingType(trainer.getSpecialization());
                }
            }
        }
        Trainee updatedTrainee = traineeRepository.save(trainee);

        log.info("Transaction ID: {}, Successfully updated trainee trainer list with trainee username: {}", transactionId, traineeUsername);
        return updatedTrainee.getTrainings().stream().map(t -> new TrainerResponseDTO(
                t.getTrainer().getUser().getUsername(),
                t.getTrainer().getUser().getFirstName(),
                t.getTrainer().getUser().getLastName(),
                new TrainingTypeDTO(t.getTrainingType().getId(), t.getTrainingType().getTrainingTypeName())
        )).toList();
    }
}
