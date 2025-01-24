package com.epam.spring.controller;

import com.epam.spring.dto.request.trainer.CreateTrainerRequestDTO;
import com.epam.spring.dto.request.trainer.UpdateTrainerRequestDTO;
import com.epam.spring.dto.response.TrainingTypeDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainer.FetchTrainerResponseDTO;
import com.epam.spring.dto.response.trainer.UpdateTrainerResponseDTO;
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

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private TrainerService trainerService;
    private TrainingService trainingService;

    @Captor
    private ArgumentCaptor<CreateTrainerRequestDTO> createTrainerCaptor;
    @Captor
    private ArgumentCaptor<UpdateTrainerRequestDTO> updateTrainerCaptor;
    @Captor
    private ArgumentCaptor<String> usernameCaptor;

    @BeforeEach
    void setup() {
        trainerService = mock(TrainerService.class);
        trainingService = mock(TrainingService.class);
        objectMapper = new ObjectMapper();
        TrainerController trainerController = new TrainerController(trainerService, trainingService);

        mockMvc = MockMvcBuilders.standaloneSetup(trainerController).build();
    }

    @Test
    public void testRegister() throws Exception {
        CreateTrainerRequestDTO request = new CreateTrainerRequestDTO();
        request.setFirstName("firstname");
        request.setLastName("lastname");

        UserCredentialsResponseDTO expectedResponse = new UserCredentialsResponseDTO("trainerUser", "token123");

        when(trainerService.create(any(CreateTrainerRequestDTO.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("trainerUser"));

        verify(trainerService).create(createTrainerCaptor.capture());

        CreateTrainerRequestDTO capturedRequest = createTrainerCaptor.getValue();
        assertEquals("firstname", capturedRequest.getFirstName());
        assertEquals("lastname", capturedRequest.getLastName());
    }

    @Test
    public void testGetTraineeProfile() throws Exception {
        String username = "trainerUser";
        FetchTrainerResponseDTO expectedResponse = new FetchTrainerResponseDTO("John", "Doe", 1L, true, new ArrayList<>());

        when(trainerService.getUserProfile(username)).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/v1/trainers")
                        .param("username", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.active").value(true));

        verify(trainerService).getUserProfile(usernameCaptor.capture());

        String capturedUsername = usernameCaptor.getValue();
        assertEquals(username, capturedUsername);
    }

//    @Test
    public void testUpdateProfile() throws Exception {
        String username = "trainerUser";
        UpdateTrainerRequestDTO request = new UpdateTrainerRequestDTO();

        UpdateTrainerResponseDTO expectedResponse = new UpdateTrainerResponseDTO("trainerUser", "John", "Doe", new TrainingTypeDTO(1L, "Fitness"), true, new ArrayList<>());

        when(trainerService.updateProfile(eq(username), eq(request))).thenReturn(expectedResponse);

        mockMvc.perform(put("/api/v1/trainers")
                        .param("username", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("trainerUser"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.active").value(true));

        verify(trainerService).updateProfile(eq(username), updateTrainerCaptor.capture());

        UpdateTrainerRequestDTO capturedRequest = updateTrainerCaptor.getValue();
        assertEquals("John", capturedRequest.getFirstName());
        assertEquals("Doe", capturedRequest.getLastName());
        assertEquals(1L, capturedRequest.getSpecializationId());
    }
}