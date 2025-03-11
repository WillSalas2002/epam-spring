package com.epam.spring.service.impl;

import com.epam.spring.dto.request.training.CreateTrainingRequestDTO;
import com.epam.spring.dto.request.training.FetchTraineeTrainingsRequestDTO;
import com.epam.spring.dto.request.training.FetchTrainerTrainingsRequestDTO;
import com.epam.spring.dto.response.training.FetchUserTrainingsResponseDTO;
import com.epam.spring.entity.TrainingRequest;
import com.epam.spring.enums.ActionType;
import com.epam.spring.error.exception.ResourceNotFoundException;
import com.epam.spring.mapper.TrainingMapper;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.repository.TraineeRepository;
import com.epam.spring.repository.TrainerRepository;
import com.epam.spring.repository.TrainingRepository;
import com.epam.spring.service.base.TrainingSpecificOperationsService;
import com.epam.spring.util.TransactionContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Transactional
@AllArgsConstructor
@Service
public class TrainingService implements TrainingSpecificOperationsService {

    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingMapper trainingMapper;
    private final RestTemplate restTemplate;

    @Override
    public void create(CreateTrainingRequestDTO createTrainingRequest) {
        String traineeUsername = createTrainingRequest.getTraineeUsername();
        String trainerUsername = createTrainingRequest.getTrainerUsername();
        String transactionId = TransactionContext.getTransactionId();
        log.info("Transaction ID: {}, Starting creation of training for trainee: {}, trainer: {}",
                transactionId, traineeUsername, trainerUsername);

        Trainee trainee = traineeRepository.findByUsername(traineeUsername).orElseThrow(() -> new ResourceNotFoundException(traineeUsername));
        Trainer trainer = trainerRepository.findByUsername(trainerUsername).orElseThrow(() -> new ResourceNotFoundException(trainerUsername));
        Training training = trainingMapper.fromCreateTrainingRequestToTraining(createTrainingRequest, trainee, trainer);
        TrainingRequest trainingRequest = TrainingRequest.builder()
                .actionType(ActionType.ADD)
                .username(trainer.getUser().getUsername())
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .date(training.getDate().atStartOfDay())
                .duration(training.getDuration())
                .isActive(trainer.getUser().isActive())
                .build();

        HttpEntity<TrainingRequest> request = new HttpEntity<>(trainingRequest);
        restTemplate.exchange("http://localhost:8081/api/v1/trainings", HttpMethod.POST, request, Void.class);
        trainingRepository.save(training);
        log.info("Transaction ID: {}, Successfully created training with id: {}", transactionId, training.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FetchUserTrainingsResponseDTO> findTraineeTrainings(FetchTraineeTrainingsRequestDTO fetchTraineeTrainingsRequest) {
        String transactionId = TransactionContext.getTransactionId();
        String traineeUsername = fetchTraineeTrainingsRequest.getTraineeUsername();
        log.info("Transaction ID: {}, Fetching trainings for trainee: {}",
                transactionId, traineeUsername);

        traineeRepository.findByUsername(traineeUsername)
                .orElseThrow(() -> new ResourceNotFoundException(traineeUsername));

        List<Training> traineeTrainings = trainingRepository.findTraineeTrainings(
                traineeUsername,
                fetchTraineeTrainingsRequest.getFromDate(),
                fetchTraineeTrainingsRequest.getToDate(),
                fetchTraineeTrainingsRequest.getTrainerUsername(),
                fetchTraineeTrainingsRequest.getTrainingTypeName()
        );
        log.info("Transaction ID: {}, Successfully fetched trainings for trainee: {}, size: {}",
                transactionId, traineeUsername, traineeTrainings.size());
        return trainingMapper.fromUserListToFetchUserTrainingsResponseList(traineeTrainings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FetchUserTrainingsResponseDTO> findTrainerTrainings(FetchTrainerTrainingsRequestDTO fetchTrainerTrainingsRequest) {
        String transactionId = TransactionContext.getTransactionId();
        String trainerUsername = fetchTrainerTrainingsRequest.getTrainerUsername();
        log.info("Transaction ID: {}, Fetching trainings for trainer: {}",
                transactionId, trainerUsername);

        trainerRepository.findByUsername(fetchTrainerTrainingsRequest.getTrainerUsername())
                .orElseThrow(() -> new ResourceNotFoundException(trainerUsername));

        List<Training> trainerTrainings = trainingRepository.findTrainerTrainings(
                trainerUsername,
                fetchTrainerTrainingsRequest.getFromDate(),
                fetchTrainerTrainingsRequest.getToDate(),
                fetchTrainerTrainingsRequest.getTraineeUsername()
        );
        log.info("Transaction ID: {}, Successfully fetched trainings for trainer: {}, size: {}",
                transactionId, trainerUsername, trainerTrainings.size());
        return trainingMapper.fromUserListToFetchUserTrainingsResponseList(trainerTrainings);
    }
}
