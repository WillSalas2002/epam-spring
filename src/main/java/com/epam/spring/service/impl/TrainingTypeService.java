package com.epam.spring.service.impl;

import com.epam.spring.dto.response.TrainingTypeDTO;
import com.epam.spring.mapper.TrainingTypeMapper;
import com.epam.spring.repository.impl.TrainingTypeRepository;
import com.epam.spring.service.base.TrainingTypeOperationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingTypeService implements TrainingTypeOperationsService {

    private final TrainingTypeRepository repository;
    private final TrainingTypeMapper trainingTypeMapper;

    @Override
    public List<TrainingTypeDTO> findAll() {
        return trainingTypeMapper.fromEntityListToDTOList(repository.findAll());
    }
}
