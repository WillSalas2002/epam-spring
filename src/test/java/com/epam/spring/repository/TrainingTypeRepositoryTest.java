package com.epam.spring.repository;

import com.epam.spring.config.TestConfig;
import com.epam.spring.model.TrainingType;
import com.epam.spring.repository.impl.TrainingTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
class TrainingTypeRepositoryTest {

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @Test
    public void testFindAll() {
        List<TrainingType> trainingTypes = trainingTypeRepository.findAll();
        assertEquals(3, trainingTypes.size());
    }
}