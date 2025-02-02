package com.epam.spring.service.impl;

import com.epam.spring.dto.response.TrainingTypeDTO;
import com.epam.spring.mapper.TrainingTypeMapper;
import com.epam.spring.model.TrainingType;
import com.epam.spring.repository.TrainingTypeRepository;
import com.epam.spring.service.base.TrainingTypeOperationsService;
import com.epam.spring.util.TransactionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TrainingTypeService implements TrainingTypeOperationsService {

    private final TrainingTypeRepository repository;
    private final TrainingTypeMapper trainingTypeMapper;

    @Override
    public List<TrainingTypeDTO> findAll() {
        log.info("Transaction ID: {}, Fetching all trainings", TransactionContext.getTransactionId());
        return trainingTypeMapper.fromEntityListToDTOList(repository.findAll());
    }

    public TrainingTypeDTO findById(Long id) {
        log.info("Transaction ID: {}, Fetching training type with id: {}",
                TransactionContext.getTransactionId(), id);
        TrainingType trainingType = repository.findById(id).orElseThrow(NoSuchElementException::new);
        return trainingTypeMapper.fromEntityToDTO(trainingType);
    }
}
