package com.epam.spring.service.impl;

import com.epam.spring.dto.request.trainer.CreateTrainerRequestDTO;
import com.epam.spring.dto.request.trainer.UpdateTrainerRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainer.FetchTrainerResponseDTO;
import com.epam.spring.dto.response.trainer.TrainerResponseDTO;
import com.epam.spring.dto.response.trainer.UpdateTrainerResponseDTO;
import com.epam.spring.error.exception.ResourceNotFoundException;
import com.epam.spring.mapper.TrainerMapper;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.TrainingType;
import com.epam.spring.repository.TraineeRepository;
import com.epam.spring.repository.TrainerRepository;
import com.epam.spring.repository.TrainingTypeRepository;
import com.epam.spring.service.base.TrainerSpecificOperationsService;
import com.epam.spring.util.PasswordGenerator;
import com.epam.spring.util.TransactionContext;
import com.epam.spring.util.UsernameGenerator;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class TrainerService implements TrainerSpecificOperationsService {

    private final UsernameGenerator usernameGenerator;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final PasswordGenerator passwordGenerator;
    private final TrainerMapper trainerMapper;
    private final TraineeRepository traineeRepository;
    private final PasswordEncoder passwordEncoder;

    private final Counter trainerCreationCounter;
    private final Timer trainerCreationTimer;

    public TrainerService(UsernameGenerator usernameGenerator,
                          TrainerRepository trainerRepository,
                          TrainingTypeRepository trainingTypeRepository,
                          PasswordGenerator passwordGenerator,
                          TrainerMapper trainerMapper,
                          TraineeRepository traineeRepository,
                          PasswordEncoder passwordEncoder,
                          MeterRegistry meterRegistry) {
        this.usernameGenerator = usernameGenerator;
        this.trainerRepository = trainerRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.passwordGenerator = passwordGenerator;
        this.trainerMapper = trainerMapper;
        this.traineeRepository = traineeRepository;
        this.passwordEncoder = passwordEncoder;

        this.trainerCreationCounter = Counter.builder("trainer_creation_total")
                .description("Total number of trainer creations")
                .register(meterRegistry);
        this.trainerCreationTimer = Timer.builder("trainer_creation_duration_seconds")
                .description("Time taken to record trainer creation")
                .register(meterRegistry);
    }

    @Override
    public UserCredentialsResponseDTO create(CreateTrainerRequestDTO createRequest) {
        return trainerCreationTimer.record(() -> {  // Start timing
            String transactionId = TransactionContext.getTransactionId();
            log.info("Transaction ID: {}, Starting trainer creation for firstName: {}, lastName: {}",
                    transactionId, createRequest.getFirstName(), createRequest.getLastName());

            log.info("Transaction ID: {}, Fetching training type with id: {}", transactionId, createRequest.getTrainingTypeId());
            TrainingType trainingType = trainingTypeRepository.findById(createRequest.getTrainingTypeId())
                    .orElseThrow(ResourceNotFoundException::new);

            String uniqueUsername = usernameGenerator.generateUniqueUsername(createRequest.getFirstName(), createRequest.getLastName());
            String password = passwordGenerator.generatePassword();

            log.info("Transaction ID: {}, Generated username: {}", transactionId, uniqueUsername);
            Trainer trainer = trainerMapper.fromCreateTrainerRequestToTrainer(createRequest, uniqueUsername, passwordEncoder.encode(password));
            trainer.setSpecialization(trainingType);

            trainerRepository.save(trainer);
            trainerCreationCounter.increment();
            log.info("Transaction ID: {}, Successfully created trainer with username: {}", transactionId, uniqueUsername);

            return new UserCredentialsResponseDTO(uniqueUsername, password);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public FetchTrainerResponseDTO getUserProfile(String username) {
        String transactionId = TransactionContext.getTransactionId();
        log.info("Transaction ID: {}, Fetching trainer with username: {}", transactionId, username);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(ResourceNotFoundException::new);
        log.info("Transaction ID: {}, Successfully fetched trainer with username: {}", transactionId, username);

        return trainerMapper.fromTrainerToFetchTrainerResponse(trainer);
    }

    @Override
    public UpdateTrainerResponseDTO updateProfile(UpdateTrainerRequestDTO updateRequest) {
        String transactionId = TransactionContext.getTransactionId();
        String username = updateRequest.getUsername();
        log.info("Transaction ID: {}, Starting to update trainer with username: {}", transactionId, username);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(ResourceNotFoundException::new);
        trainerMapper.fromUpdateTrainerRequestToTrainer(trainer, updateRequest);

        Trainer updatedTrainer = trainerRepository.save(trainer);
        log.info("Transaction ID: {}, Successfully updated trainer with username: {}", transactionId, username);

        return trainerMapper.fromTrainerToUpdatedTrainerResponse(updatedTrainer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerResponseDTO> findUnassignedTrainersByTraineeUsername(String username) {
        String transactionId = TransactionContext.getTransactionId();
        log.info("Transaction ID: {}, Fetching unassigned trainers for trainee with username: {}", transactionId, username);

        traineeRepository.findByUsername(username).orElseThrow(ResourceNotFoundException::new);

        List<Trainer> trainers = trainerRepository.findUnassignedTrainersByTraineeUsername(username);
        log.info("Transaction ID: {}, Successfully fetched unassigned trainers for trainee with username: {}", transactionId, username);

        return trainerMapper.fromTrainerListToTrainerResponseDTOList(trainers);
    }
}
