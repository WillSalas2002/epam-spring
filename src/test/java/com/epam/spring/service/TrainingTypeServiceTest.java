package com.epam.spring.service;

import com.epam.spring.dto.response.TrainingTypeDTO;
import com.epam.spring.service.impl.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Rollback
@Transactional
class TrainingTypeServiceTest {

    @Autowired
    private TrainingTypeService trainingTypeService;

    @Test
    public void testFindAll() {
        List<TrainingTypeDTO> trainingTypes = trainingTypeService.findAll();
        assertEquals(3, trainingTypes.size());
    }

    @Test
    public void testFindById() {
        TrainingTypeDTO trainingTypeDTO = trainingTypeService.findById(1L);
        assertNotNull(trainingTypeDTO);
        assertEquals(1L, trainingTypeDTO.getId());
    }
}