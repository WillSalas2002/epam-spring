package com.epam.spring.controller;

import com.epam.spring.service.impl.TraineeService;
import com.epam.spring.service.impl.TrainerService;
import com.epam.spring.service.impl.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
class TraineeControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TraineeService traineeService;

    @InjectMocks
    private TrainerService trainerService;

    @InjectMocks
    private TrainingService trainingService;

    @BeforeEach
    public void setup() {
        traineeService = mock(TraineeService.class);

        TraineeController trainingController = new TraineeController(traineeService, trainerService, trainingService);

        mockMvc = MockMvcBuilders.standaloneSetup(trainingController).build();
    }
}