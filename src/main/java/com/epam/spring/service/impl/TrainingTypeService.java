package com.epam.spring.service.impl;

import com.epam.spring.dto.response.TrainingTypeDTO;
import com.epam.spring.repository.impl.TrainingTypeRepository;
import com.epam.spring.service.base.TrainingTypeOperationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingTypeService implements TrainingTypeOperationsService {

    private final TrainingTypeRepository repository;

    @Override
    public List<TrainingTypeDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(trainingType -> new TrainingTypeDTO(trainingType.getId(), trainingType.getTrainingTypeName()))
                .toList();
    }
}
