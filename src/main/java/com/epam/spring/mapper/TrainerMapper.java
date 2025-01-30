package com.epam.spring.mapper;

import com.epam.spring.dto.request.trainer.CreateTrainerRequestDTO;
import com.epam.spring.dto.request.trainer.UpdateTrainerRequestDTO;
import com.epam.spring.dto.response.TrainingTypeDTO;
import com.epam.spring.dto.response.trainee.TraineeResponseDTO;
import com.epam.spring.dto.response.trainer.FetchTrainerResponseDTO;
import com.epam.spring.dto.response.trainer.TrainerResponseDTO;
import com.epam.spring.dto.response.trainer.UpdateTrainerResponseDTO;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.TrainingType;
import com.epam.spring.model.User;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("singleton")
public class TrainerMapper {

    public Trainer fromCreateTrainerRequestToTrainer(CreateTrainerRequestDTO createRequest, String username, String password) {
        return Trainer.builder()
                .user(User.builder()
                        .firstName(createRequest.getFirstName())
                        .lastName(createRequest.getLastName())
                        .username(username)
                        .password(password)
                        .isActive(Boolean.FALSE)
                        .build())
                .specialization(TrainingType.builder().id(Long.valueOf(createRequest.getTrainingTypeId())).build())
                .build();
    }

    public void fromUpdateTrainerRequestToTrainer(Trainer trainer, UpdateTrainerRequestDTO updateRequest) {
        User user = trainer.getUser();
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setActive(updateRequest.getActive());
        TrainingType specialization = new TrainingType();
        specialization.setId(Long.valueOf(updateRequest.getSpecializationId()));
        trainer.setSpecialization(specialization);
    }

    public List<TrainerResponseDTO> fromTrainerListToTrainerResponseDTOList(List<Trainer> trainers) {
        return trainers.stream()
                .map(trainer -> new TrainerResponseDTO(
                        trainer.getUser().getUsername(),
                        trainer.getUser().getFirstName(),
                        trainer.getUser().getLastName(),
                        new TrainingTypeDTO(
                                trainer.getSpecialization().getId(),
                                trainer.getSpecialization().getTrainingTypeName()
                        ))).toList();
    }

    public UpdateTrainerResponseDTO fromTrainerToUpdatedTrainerResponse(Trainer trainer) {
        return UpdateTrainerResponseDTO.builder()
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .username(trainer.getUser().getUsername())
                .active(trainer.getUser().isActive())
                .specialization(new TrainingTypeDTO(trainer.getSpecialization().getId(), trainer.getSpecialization().getTrainingTypeName()))
                .trainees(trainer.getTrainings().stream()
                        .map(training -> new TraineeResponseDTO(
                                training.getTrainee().getUser().getUsername(),
                                training.getTrainee().getUser().getFirstName(),
                                training.getTrainee().getUser().getFirstName()
                        )).toList())
                .build();
    }

    public FetchTrainerResponseDTO fromTrainerToFetchTrainerResponse(Trainer trainer) {
        List<TraineeResponseDTO> trainees = trainer.getTrainings().stream()
                .map(training -> new TraineeResponseDTO(
                        training.getTrainee().getUser().getUsername(),
                        training.getTrainee().getUser().getFirstName(),
                        training.getTrainee().getUser().getLastName()
                )).toList();
        return FetchTrainerResponseDTO.builder()
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .active(trainer.getUser().isActive())
                .specializationId(trainer.getSpecialization().getId())
                .trainees(trainees)
                .build();
    }
}
