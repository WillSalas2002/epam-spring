package com.epam.spring.service.impl;

import com.epam.spring.dto.request.training.CreateTrainingRequestDTO;
import com.epam.spring.dto.request.training.FetchTraineeTrainingsRequestDTO;
import com.epam.spring.dto.request.training.FetchTrainerTrainingsRequestDTO;
import com.epam.spring.dto.response.training.FetchUserTrainingsResponseDTO;
import com.epam.spring.error.exception.ResourceNotFoundException;
import com.epam.spring.mapper.TrainingMapper;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.repository.implnew.TraineeRepository;
import com.epam.spring.repository.implnew.TrainerRepository;
import com.epam.spring.repository.implnew.TrainingRepository;
import com.epam.spring.service.base.TrainingSpecificOperationsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class TrainingService implements TrainingSpecificOperationsService {

    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingMapper trainingMapper;

    @Override
    public void create(CreateTrainingRequestDTO createTrainingRequest) {
        String traineeUsername = createTrainingRequest.getTraineeUsername();
        String trainerUsername = createTrainingRequest.getTrainerUsername();

        Trainee trainee = traineeRepository.findByUsername(traineeUsername).orElseThrow(ResourceNotFoundException::new);
        Trainer trainer = trainerRepository.findByUsername(trainerUsername).orElseThrow(ResourceNotFoundException::new);

        Training training = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .name(createTrainingRequest.getTrainingName())
                .trainingType(trainer.getSpecialization())
                .duration(Integer.valueOf(createTrainingRequest.getDuration()))
                .date(LocalDate.parse(createTrainingRequest.getTrainingDate()))
                .build();
        trainingRepository.save(training);
    }

    @Override
    public List<FetchUserTrainingsResponseDTO> findTraineeTrainings(FetchTraineeTrainingsRequestDTO fetchTraineeTrainingsRequest) {
        traineeRepository.findByUsername(fetchTraineeTrainingsRequest.getTraineeUsername()).orElseThrow(ResourceNotFoundException::new);
        List<Training> traineeTrainings = trainingRepository.findTraineeTrainings(
                fetchTraineeTrainingsRequest.getTraineeUsername(),
                fetchTraineeTrainingsRequest.getFromDate(),
                fetchTraineeTrainingsRequest.getToDate(),
                fetchTraineeTrainingsRequest.getTrainerUsername(),
                fetchTraineeTrainingsRequest.getTrainingTypeName()
        );
        return trainingMapper.fromUserListToFetchUserTrainingsResponseList(traineeTrainings);
    }

    @Override
    public List<FetchUserTrainingsResponseDTO> findTrainerTrainings(FetchTrainerTrainingsRequestDTO fetchTrainerTrainingsRequest) {
        trainerRepository.findByUsername(fetchTrainerTrainingsRequest.getTrainerUsername()).orElseThrow(ResourceNotFoundException::new);
        List<Training> trainerTrainings = trainingRepository.findTrainerTrainings(
                fetchTrainerTrainingsRequest.getTrainerUsername(),
                fetchTrainerTrainingsRequest.getFromDate(),
                fetchTrainerTrainingsRequest.getToDate(),
                fetchTrainerTrainingsRequest.getTraineeUsername()
        );
        return trainingMapper.fromUserListToFetchUserTrainingsResponseList(trainerTrainings);
    }
}
