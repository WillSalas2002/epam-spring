package com.epam.spring.service;

import com.epam.spring.dto.TrainingTypeDTO;
import com.epam.spring.dto.request.training.CreateTrainingRequestDTO;
import com.epam.spring.dto.request.training.FetchTraineeTrainingsRequestDTO;
import com.epam.spring.dto.request.training.FetchTrainerTrainingsRequestDTO;
import com.epam.spring.dto.response.training.FetchUserTrainingsResponseDTO;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.repository.TraineeRepository;
import com.epam.spring.repository.TrainerRepository;
import com.epam.spring.repository.TrainingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@AllArgsConstructor
@Service
public class TrainingService implements TrainingSpecificOperationsService {

    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;

    @Override
    public void create(CreateTrainingRequestDTO createTrainingRequest) {
        String traineeUsername = createTrainingRequest.getTraineeUsername();
        String trainerUsername = createTrainingRequest.getTrainerUsername();

        Trainee trainee = traineeRepository.findByUsername(traineeUsername)
                .orElseThrow(() -> new NoSuchElementException("Trainee with username " + traineeUsername + " not exists"));
        Trainer trainer = trainerRepository.findByUsername(trainerUsername)
                .orElseThrow(() -> new NoSuchElementException("Trainer with username " + trainerUsername + " not exists"));

        Training training = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .name(createTrainingRequest.getTrainingName())
                .trainingType(trainer.getSpecialization())
                .duration(Integer.valueOf(createTrainingRequest.getDuration()))
                .date(LocalDateTime.parse(createTrainingRequest.getTrainingDate()))
                .build();

        trainingRepository.create(training);
    }

    @Override
    public List<FetchUserTrainingsResponseDTO> findTraineeTrainings(String username, FetchTraineeTrainingsRequestDTO fetchTraineeTrainingsRequest) {
        List<Training> traineeTrainings = trainingRepository.findTraineeTrainings(
                username,
                LocalDateTime.parse(fetchTraineeTrainingsRequest.getFromDate()),
                LocalDateTime.parse(fetchTraineeTrainingsRequest.getToDate()),
                fetchTraineeTrainingsRequest.getTrainerUsername(),
                fetchTraineeTrainingsRequest.getTrainingType().getTrainingTypeName());

        return traineeTrainings.stream()
                .map(training -> new FetchUserTrainingsResponseDTO(
                        training.getName(),
                        training.getDate(),
                        new TrainingTypeDTO(training.getTrainingType().getId(), training.getTrainingType().getTrainingTypeName()),
                        training.getDuration(),
                        training.getTrainee().getUser().getUsername()
                ))
                .toList();
    }

    @Override
    public List<FetchUserTrainingsResponseDTO> findTrainerTrainings(String username, FetchTrainerTrainingsRequestDTO fetchTrainerTrainingsRequest) {
        List<Training> trainerTrainings = trainingRepository.findTrainerTrainings(
                username,
                fetchTrainerTrainingsRequest.getFromDate(),
                fetchTrainerTrainingsRequest.getToDate(),
                fetchTrainerTrainingsRequest.getTraineeUsername());

        return trainerTrainings.stream()
                .map(training -> new FetchUserTrainingsResponseDTO(
                        training.getName(),
                        training.getDate(),
                        new TrainingTypeDTO(training.getTrainingType().getId(), training.getTrainingType().getTrainingTypeName()),
                        training.getDuration(),
                        training.getTrainee().getUser().getUsername()
                ))
                .toList();
    }
}
