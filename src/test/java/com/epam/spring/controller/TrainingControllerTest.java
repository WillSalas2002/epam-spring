package com.epam.spring.controller;

import com.epam.spring.dto.request.training.CreateTrainingRequestDTO;
import com.epam.spring.service.impl.TrainingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TrainingControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private TrainingService trainingService;

    @Captor
    private ArgumentCaptor<CreateTrainingRequestDTO> trainingCaptor;

    @BeforeEach
    void setup() {
        trainingService = mock(TrainingService.class);
        objectMapper = new ObjectMapper();
        TrainingController trainingController = new TrainingController(trainingService);

        mockMvc = MockMvcBuilders.standaloneSetup(trainingController).build();
    }

    @Test
    public void testCreateTraining() throws Exception {
        CreateTrainingRequestDTO request = new CreateTrainingRequestDTO();
        request.setTraineeUsername("traineeUser");
        request.setTrainerUsername("trainerUser");
        request.setTrainingName("Java Basics");
        request.setTrainingDate("2025-01-01");
        request.setDuration("2 hours");

        mockMvc.perform(post("/api/v1/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(trainingService).create(trainingCaptor.capture());

        CreateTrainingRequestDTO capturedRequest = trainingCaptor.getValue();
        assertEquals("traineeUser", capturedRequest.getTraineeUsername());
        assertEquals("trainerUser", capturedRequest.getTrainerUsername());
        assertEquals("Java Basics", capturedRequest.getTrainingName());
        assertEquals("2025-01-01", capturedRequest.getTrainingDate());
        assertEquals("2 hours", capturedRequest.getDuration());
    }
}
