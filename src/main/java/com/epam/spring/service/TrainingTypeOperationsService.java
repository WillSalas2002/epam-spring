package com.epam.spring.service;

import com.epam.spring.dto.response.TrainingTypeDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TrainingTypeOperationsService {

    List<TrainingTypeDTO> findAll();
}
