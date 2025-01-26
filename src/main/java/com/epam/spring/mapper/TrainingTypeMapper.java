package com.epam.spring.mapper;

import com.epam.spring.dto.response.TrainingTypeDTO;
import com.epam.spring.model.TrainingType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("singleton")
public class TrainingTypeMapper {

    private TrainingTypeDTO fromEntityToDTO(TrainingType trainingType) {
        return new TrainingTypeDTO(trainingType.getId(), trainingType.getTrainingTypeName());
    }

    public List<TrainingTypeDTO> fromEntityListToDTOList(List<TrainingType> trainingTypeList) {
        return trainingTypeList.stream().map(this::fromEntityToDTO).toList();
    }
}
