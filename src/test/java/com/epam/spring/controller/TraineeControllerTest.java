package com.epam.spring.controller;

import com.epam.spring.dto.request.trainee.CreateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.TrainingIdTrainerUsernamePair;
import com.epam.spring.dto.request.trainee.UpdateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.UpdateTraineeTrainerRequestDTO;
import com.epam.spring.dto.request.training.FetchTraineeTrainingsRequestDTO;
import com.epam.spring.dto.response.TrainingTypeDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainee.FetchTraineeResponseDTO;
import com.epam.spring.dto.response.trainee.UpdateTraineeResponseDTO;
import com.epam.spring.dto.response.trainer.TrainerResponseDTO;
import com.epam.spring.dto.response.training.FetchUserTrainingsResponseDTO;
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

import java.util.Arrays;
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

    @Test
    public void testFindUnassignedTrainersByTraineeUsername() throws Exception {
        String username = "traineeUser";
        List<TrainerResponseDTO> expectedResponse = Arrays.asList(
                new TrainerResponseDTO("trainer1", "John", "Doe", new TrainingTypeDTO(1L, "Fitness")),
                new TrainerResponseDTO("trainer2", "Jane", "Smith", new TrainingTypeDTO(2L, "Yoga"))
        );

        when(trainerService.findUnassignedTrainersByTraineeUsername(username)).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/v1/trainees/unassigned-trainers/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("trainer1"))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].specialization.id").value(1L))
                .andExpect(jsonPath("$[0].specialization.trainingTypeName").value("Fitness"))
                .andExpect(jsonPath("$[1].username").value("trainer2"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Smith"))
                .andExpect(jsonPath("$[1].specialization.id").value(2L))
                .andExpect(jsonPath("$[1].specialization.trainingTypeName").value("Yoga"));
    }

    @Test
    public void testGetTraineeTrainings() throws Exception {
        FetchTraineeTrainingsRequestDTO requestDTO = new FetchTraineeTrainingsRequestDTO("traineeUser");

        List<FetchUserTrainingsResponseDTO> expectedResponse = Arrays.asList(
                new FetchUserTrainingsResponseDTO("Strength Training", "2025-02-01",
                        new TrainingTypeDTO(1L, "Fitness"), 60, "trainerUser"),
                new FetchUserTrainingsResponseDTO("Yoga", "2025-02-02",
                        new TrainingTypeDTO(2L, "Yoga"), 45, "trainerUser")
        );

        when(trainingService.findTraineeTrainings(any(FetchTraineeTrainingsRequestDTO.class))).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/v1/trainees/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].trainingName").value("Strength Training"))
                .andExpect(jsonPath("$[0].trainingDate").value("2025-02-01"))
                .andExpect(jsonPath("$[0].trainingType.id").value(1L))
                .andExpect(jsonPath("$[0].trainingType.trainingTypeName").value("Fitness"))
                .andExpect(jsonPath("$[0].duration").value(60))
                .andExpect(jsonPath("$[0].username").value("trainerUser"))
                .andExpect(jsonPath("$[1].trainingName").value("Yoga"))
                .andExpect(jsonPath("$[1].trainingDate").value("2025-02-02"))
                .andExpect(jsonPath("$[1].trainingType.id").value(2L))
                .andExpect(jsonPath("$[1].trainingType.trainingTypeName").value("Yoga"))
                .andExpect(jsonPath("$[1].duration").value(45))
                .andExpect(jsonPath("$[1].username").value("trainerUser"));
    }

    @Test
    public void testUpdateTraineeTrainers() throws Exception {
        UpdateTraineeTrainerRequestDTO requestDTO = new UpdateTraineeTrainerRequestDTO();
        requestDTO.setTraineeUsername("traineeUser");
        requestDTO.setTrainingIdTrainerUsernamePairs(Arrays.asList(
                new TrainingIdTrainerUsernamePair(1L, "trainer1"),
                new TrainingIdTrainerUsernamePair(1L, "trainer2")
        ));

        List<TrainerResponseDTO> expectedResponse = Arrays.asList(
                new TrainerResponseDTO("trainer1", "John", "Doe", new TrainingTypeDTO(1L, "Fitness")),
                new TrainerResponseDTO("trainer2", "Jane", "Smith", new TrainingTypeDTO(2L, "Yoga"))
        );

        when(traineeService.updateTraineeTrainerList(any(UpdateTraineeTrainerRequestDTO.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(put("/api/v1/trainees/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("trainer1"))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].specialization.id").value(1L))
                .andExpect(jsonPath("$[0].specialization.trainingTypeName").value("Fitness"))
                .andExpect(jsonPath("$[1].username").value("trainer2"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Smith"))
                .andExpect(jsonPath("$[1].specialization.id").value(2L))
                .andExpect(jsonPath("$[1].specialization.trainingTypeName").value("Yoga"));
    }
}