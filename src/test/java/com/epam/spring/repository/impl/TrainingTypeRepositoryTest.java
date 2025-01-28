package com.epam.spring.repository.impl;

import com.epam.spring.config.TestConfig;
import com.epam.spring.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
class TrainingTypeRepositoryTest {

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @Test
    void testFindAll() {
        List<TrainingType> trainingTypes = trainingTypeRepository.findAll();
        assertEquals(3, trainingTypes.size());
    }

    @Test
    void testFindById() {
        Optional<TrainingType> trainingTypeOptional = trainingTypeRepository.findById(1L);
        assertTrue(trainingTypeOptional.isPresent());
        assertEquals(1L, trainingTypeOptional.get().getId());
    }
}