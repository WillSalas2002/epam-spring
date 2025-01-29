package com.epam.spring.controller;

import com.epam.spring.dto.request.trainee.CreateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.UpdateTraineeRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainee.FetchTraineeResponseDTO;
import com.epam.spring.dto.response.trainee.UpdateTraineeResponseDTO;
import com.epam.spring.service.impl.TraineeService;
import com.epam.spring.service.impl.TrainerService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private TraineeService traineeService;
    private TrainerService trainerService;
    private TrainingService trainingService;

    @Captor
    private ArgumentCaptor<CreateTraineeRequestDTO> createTraineeCaptor;
    @Captor
    private ArgumentCaptor<UpdateTraineeRequestDTO> updateTraineeCaptor;

    @BeforeEach
    public void setup() {
        traineeService = mock(TraineeService.class);
        trainerService = mock(TrainerService.class);
        trainingService = mock(TrainingService.class);
        objectMapper = new ObjectMapper();
        TraineeController trainerController = new TraineeController(traineeService, trainerService, trainingService);

        mockMvc = MockMvcBuilders.standaloneSetup(trainerController).build();
    }

    @Test
    public void testRegister() throws Exception {
        CreateTraineeRequestDTO request = new CreateTraineeRequestDTO();
        request.setFirstName("firstname");
        request.setLastName("lastname");

        UserCredentialsResponseDTO expectedResponse = new UserCredentialsResponseDTO("traineeUser", "token123");

        when(traineeService.create(any(CreateTraineeRequestDTO.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("traineeUser"));

        verify(traineeService).create(createTraineeCaptor.capture());

        CreateTraineeRequestDTO capturedRequest = createTraineeCaptor.getValue();
        assertEquals("firstname", capturedRequest.getFirstName());
        assertEquals("lastname", capturedRequest.getLastName());
    }

    @Test
    public void testGetTraineeProfile() throws Exception {
        String username = "username";
        FetchTraineeResponseDTO expectedResponse = FetchTraineeResponseDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .isActive(true)
                .trainers(List.of())
                .build();

        when(traineeService.getUserProfile(username)).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/v1/trainees/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("firstName"))
                .andExpect(jsonPath("$.lastName").value("lastName"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.trainers").isEmpty());
    }

    @Test
    public void testUpdateProfile() throws Exception {
        String username = "username";
        String firstName = "firstName";
        String lastName = "lastName";
        UpdateTraineeRequestDTO request = UpdateTraineeRequestDTO.builder()
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .isActive(true)
                .build();
        UpdateTraineeResponseDTO expectedResponse = UpdateTraineeResponseDTO.builder()
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .isActive(true)
                .trainers(List.of())
                .build();

        when(traineeService.updateProfile(any(UpdateTraineeRequestDTO.class))).thenReturn(expectedResponse);

        mockMvc.perform(put("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.trainers").isEmpty());

        verify(traineeService).updateProfile(updateTraineeCaptor.capture());

        UpdateTraineeRequestDTO capturedRequest = updateTraineeCaptor.getValue();
        assertEquals(firstName, capturedRequest.getFirstName());
        assertEquals(lastName, capturedRequest.getLastName());
    }
}