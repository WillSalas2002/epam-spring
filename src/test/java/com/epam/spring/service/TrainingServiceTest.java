package com.epam.spring.service;

import com.epam.spring.dto.request.trainee.CreateTraineeRequestDTO;
import com.epam.spring.dto.request.trainer.CreateTrainerRequestDTO;
import com.epam.spring.dto.request.training.CreateTrainingRequestDTO;
import com.epam.spring.dto.request.training.FetchTraineeTrainingsRequestDTO;
import com.epam.spring.dto.request.training.FetchTrainerTrainingsRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.training.FetchUserTrainingsResponseDTO;
import com.epam.spring.service.impl.TraineeService;
import com.epam.spring.service.impl.TrainerService;
import com.epam.spring.service.impl.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Rollback
@Transactional
class TrainingServiceTest {

    @Autowired
    private TrainingService trainingService;
    @Autowired
    private TraineeService traineeService;
    @Autowired
    private TrainerService trainerService;

    private CreateTraineeRequestDTO createTraineeRequest;
    private CreateTrainerRequestDTO createTrainerRequest;

    @BeforeEach
    void setUp() {
        createTraineeRequest = buildCreateTraineeRequestDTO("Trainee", "Trainee");
        createTrainerRequest = buildCreateTrainerRequest("Trainer", "Trainer", 1L);
    }

    @Test
    public void testFindTraineeAndTrainerTrainings() {
        UserCredentialsResponseDTO traineeResponse = traineeService.create(createTraineeRequest);
        UserCredentialsResponseDTO trainerResponse = trainerService.create(createTrainerRequest);

        CreateTrainingRequestDTO trainingRequest1 = buildTrainingRequest(traineeResponse.getUsername(), trainerResponse.getUsername());

        CreateTrainerRequestDTO trainerRequest2 = buildCreateTrainerRequest("Trainer2", "Trainer2", 2L);
        UserCredentialsResponseDTO trainerResponse2 = trainerService.create(trainerRequest2);
        CreateTrainingRequestDTO trainingRequest2 = buildTrainingRequest(traineeResponse.getUsername(), trainerResponse2.getUsername());

        trainingService.create(trainingRequest1);
        trainingService.create(trainingRequest2);

        FetchTraineeTrainingsRequestDTO fetchTraineeTrainingsRequest = new FetchTraineeTrainingsRequestDTO(traineeResponse.getUsername());
        FetchTrainerTrainingsRequestDTO fetchTrainerTrainingsRequest = new FetchTrainerTrainingsRequestDTO(trainerResponse.getUsername());

        List<FetchUserTrainingsResponseDTO> traineeTrainings = trainingService.findTraineeTrainings(fetchTraineeTrainingsRequest);
        List<FetchUserTrainingsResponseDTO> trainerTrainings = trainingService.findTrainerTrainings(fetchTrainerTrainingsRequest);

        assertEquals(2, traineeTrainings.size());
        assertEquals(1, trainerTrainings.size());
    }

    private CreateTrainingRequestDTO buildTrainingRequest(String traineeUsername, String trainerUsername) {
        return CreateTrainingRequestDTO.builder()
                .traineeUsername(traineeUsername)
                .trainerUsername(trainerUsername)
                .trainingName("Cardio")
                .trainingDate(LocalDate.now().plusDays(1).toString())
                .duration(String.valueOf(90))
                .build();
    }

    private static CreateTraineeRequestDTO buildCreateTraineeRequestDTO(String firstName, String lastName) {
        return CreateTraineeRequestDTO.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(LocalDate.now().minusYears(15).toString())
                .address("Street 77")
                .build();
    }

    private static CreateTrainerRequestDTO buildCreateTrainerRequest(String firstName, String lastName, Long trainingTypeId) {
        return CreateTrainerRequestDTO.builder()
                .firstName(firstName)
                .lastName(lastName)
                .trainingTypeId(trainingTypeId)
                .build();
    }
}
