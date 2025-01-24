package com.epam.spring.service;

import com.epam.spring.config.TestConfig;
import com.epam.spring.dto.response.TrainingTypeDTO;
import com.epam.spring.service.impl.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
class TrainingTypeServiceTest {

    @Autowired
    private TrainingTypeService trainingTypeService;

    @Test
    public void testFindAll() {
        List<TrainingTypeDTO> trainingTypes = trainingTypeService.findAll();
        assertEquals(3, trainingTypes.size());
    }
}