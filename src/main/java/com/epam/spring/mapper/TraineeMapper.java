package com.epam.spring.mapper;

import com.epam.spring.dto.request.trainee.CreateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.UpdateTraineeRequestDTO;
import com.epam.spring.dto.response.TrainingTypeDTO;
import com.epam.spring.dto.response.trainee.FetchTraineeResponseDTO;
import com.epam.spring.dto.response.trainee.UpdateTraineeResponseDTO;
import com.epam.spring.dto.response.trainer.TrainerResponseDTO;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.User;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Scope("singleton")
public class TraineeMapper {

    public Trainee fromCreateTraineeRequestToTrainee(CreateTraineeRequestDTO createRequest, String username, String password) {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setFirstName(createRequest.getFirstName());
        user.setLastName(createRequest.getLastName());
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(false);
        trainee.setUser(user);
        trainee.setAddress(createRequest.getAddress());
        if (createRequest.getDateOfBirth() != null) {
            trainee.setDataOfBirth(LocalDate.parse(createRequest.getDateOfBirth()));
        }
        return trainee;
    }

    public void fromUpdateTraineeRequestToTrainee(Trainee trainee, UpdateTraineeRequestDTO updateRequest) {
        User user = trainee.getUser();
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setActive(updateRequest.getIsActive());
        trainee.setAddress(updateRequest.getAddress());
        if (updateRequest.getDateOfBirth() != null) {
            trainee.setDataOfBirth(LocalDate.parse(updateRequest.getDateOfBirth()));
        }
    }

    public UpdateTraineeResponseDTO fromTraineeToUpdateTraineeResponse(Trainee trainee) {
        List<TrainerResponseDTO> trainers = null;
        if (trainee.getTrainings() != null) {
            trainers = trainee.getTrainings()
                    .stream()
                    .map(tgs -> new TrainerResponseDTO(
                                    tgs.getTrainer().getUser().getUsername(),
                                    tgs.getTrainer().getUser().getFirstName(),
                                    tgs.getTrainer().getUser().getLastName(),
                                    new TrainingTypeDTO(
                                            tgs.getTrainingType().getId(),
                                            tgs.getTrainingType().getTrainingTypeName()
                                    )
                            )

                    ).toList();
        }
        UpdateTraineeResponseDTO response = UpdateTraineeResponseDTO.builder()
                .username(trainee.getUser().getUsername())
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .isActive(trainee.getUser().isActive())
                .address(trainee.getAddress())
                .trainers(trainers)
                .build();
        if (trainee.getDataOfBirth() != null) {
            response.setDateOfBirth(String.valueOf(trainee.getDataOfBirth()));
        }
        return response;
    }

    public FetchTraineeResponseDTO fromTraineeToFetchTraineeResponse(Trainee trainee) {
        User user = trainee.getUser();
        List<TrainerResponseDTO> trainers = null;

        if (trainee.getTrainings() != null) {
            trainers = trainee.getTrainings().stream().map(training -> new TrainerResponseDTO(
                    training.getTrainer().getUser().getUsername(),
                    training.getTrainer().getUser().getFirstName(),
                    training.getTrainer().getUser().getLastName(),
                    new TrainingTypeDTO(training.getTrainingType().getId(), training.getTrainingType().getTrainingTypeName())
            )).toList();
        }

        FetchTraineeResponseDTO response = FetchTraineeResponseDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .address(trainee.getAddress())
                .isActive(trainee.getUser().isActive())
                .trainers(trainers)
                .build();
        if (trainee.getDataOfBirth() != null) {
            response.setDateOfBirth(String.valueOf(trainee.getDataOfBirth()));
        }
        return response;
    }
}
