package com.epam.spring.mapper;

import com.epam.spring.dto.response.TrainingTypeDTO;
import com.epam.spring.dto.response.training.FetchUserTrainingsResponseDTO;
import com.epam.spring.model.Training;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("singleton")
public class TrainingMapper {

    public List<FetchUserTrainingsResponseDTO> fromUserListToFetchUserTrainingsResponseList(List<Training> trainings) {
        return trainings.stream()
                .map(training -> new FetchUserTrainingsResponseDTO(
                        training.getName(),
                        training.getDate().toString(),
                        new TrainingTypeDTO(training.getTrainingType().getId(), training.getTrainingType().getTrainingTypeName()),
                        training.getDuration(),
                        training.getTrainee().getUser().getUsername()
                ))
                .toList();
    }
}
