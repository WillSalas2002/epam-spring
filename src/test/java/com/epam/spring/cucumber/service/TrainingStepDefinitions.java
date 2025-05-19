package com.epam.spring.cucumber.service;

import com.epam.spring.client.TrainingMQProducer;
import com.epam.spring.dto.request.training.CreateTrainingRequestDTO;
import com.epam.spring.dto.request.training.FetchTraineeTrainingsRequestDTO;
import com.epam.spring.dto.request.training.FetchTrainerTrainingsRequestDTO;
import com.epam.spring.dto.response.training.FetchUserTrainingsResponseDTO;
import com.epam.spring.entity.TrainingRequest;
import com.epam.spring.mapper.TrainingMapper;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.User;
import com.epam.spring.repository.TraineeRepository;
import com.epam.spring.repository.TrainerRepository;
import com.epam.spring.repository.TrainingRepository;
import com.epam.spring.service.impl.TrainingService;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrainingStepDefinitions {

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainingMapper trainingMapper;
    @Mock
    private TrainingMQProducer trainingMQProducer;

    @InjectMocks
    private TrainingService trainingService;

    private List<FetchUserTrainingsResponseDTO> result;

    private AutoCloseable autoCloseable;

    @Before
    public void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @After
    public void cleanup() throws Exception {
        autoCloseable.close();
    }

        private CreateTrainingRequestDTO createRequest;
    private FetchTraineeTrainingsRequestDTO traineeTrainingsRequest;
    private FetchTrainerTrainingsRequestDTO trainerTrainingsRequest;
    private List<Training> mockTrainings;
    private List<FetchUserTrainingsResponseDTO> responseDTOList;

    @Given("a valid create training request")
    public void a_valid_create_training_request() {
        String trainerUsername = "Simon.Kim";
        createRequest = new CreateTrainingRequestDTO();
        createRequest.setTraineeUsername("John.Doe");
        createRequest.setTrainerUsername(trainerUsername);

        Trainee trainee = new Trainee();
        User user = User.builder()
                .username(trainerUsername)
                .firstName(trainerUsername.split("\\.")[0])
                .username(trainerUsername.split("\\.")[1])
                .isActive(true)
                .build();
        Trainer trainer = Trainer.builder()
                .user(user)
                .build();
        Training training = Training.builder()
                .id(1L)
                .duration(120)
                .date(LocalDate.now())
                .build();
        training.setId(1L);

        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("Simon.Kim")).thenReturn(Optional.of(trainer));
        when(trainingMapper.fromCreateTrainingRequestToTraining(createRequest, trainee, trainer)).thenReturn(training);
    }

    @When("the create method is called")
    public void the_create_method_is_called() {
        trainingService.create(createRequest);
    }

    @Then("a training should be saved and a message should be sent to MQ")
    public void a_training_should_be_saved_and_message_sent() {
        verify(trainingRepository, times(1)).save(any(Training.class));
        verify(trainingMQProducer, times(1)).sendMessageToTrainingQueue(any(TrainingRequest.class));
    }

    @Given("a valid trainee username and date range")
    public void a_valid_trainee_username_and_date_range() {
        traineeTrainingsRequest = new FetchTraineeTrainingsRequestDTO();
        traineeTrainingsRequest.setTraineeUsername("trainee1");
        traineeTrainingsRequest.setFromDate(LocalDate.of(2024, 1, 1));
        traineeTrainingsRequest.setToDate(LocalDate.of(2024, 12, 31));
        traineeTrainingsRequest.setTrainerUsername("trainer1");
        traineeTrainingsRequest.setTrainingTypeName("Strength");

        Trainee trainee = new Trainee();
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        Training training = new Training();
        training.setId(1L);
        training.setDate(LocalDate.of(2024, 4, 10));

        mockTrainings = List.of(training);
        responseDTOList = List.of(new FetchUserTrainingsResponseDTO());

        when(trainingRepository.findTraineeTrainings(
                eq("trainee1"),
                any(LocalDate.class),
                any(LocalDate.class),
                eq("trainer1"),
                eq("Strength")
        )).thenReturn(mockTrainings);

        when(trainingMapper.fromUserListToFetchUserTrainingsResponseList(mockTrainings)).thenReturn(responseDTOList);
    }

    @When("the findTraineeTrainings method is called")
    public void the_findTraineeTrainings_method_is_called() {
        result = trainingService.findTraineeTrainings(traineeTrainingsRequest);
    }

    @Then("a list of trainee trainings should be returned")
    public void a_list_of_trainee_trainings_should_be_returned() {
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(traineeRepository, times(1)).findByUsername("trainee1");
        verify(trainingRepository, times(1)).findTraineeTrainings(
                anyString(), any(), any(), any(), any()
        );
        verify(trainingMapper, times(1)).fromUserListToFetchUserTrainingsResponseList(mockTrainings);
    }

    @Given("a valid trainer username and date range")
    public void a_valid_trainer_username_and_date_range() {
        trainerTrainingsRequest = new FetchTrainerTrainingsRequestDTO();
        trainerTrainingsRequest.setTrainerUsername("trainer1");
        trainerTrainingsRequest.setFromDate(LocalDate.of(2024, 1, 1));
        trainerTrainingsRequest.setToDate(LocalDate.of(2024, 12, 31));
        trainerTrainingsRequest.setTraineeUsername("trainee1");

        Trainer trainer = new Trainer();
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        Training training = new Training();
        training.setId(2L);
        training.setDate(LocalDate.of(2024, 5, 15));

        mockTrainings = List.of(training);
        responseDTOList = List.of(new FetchUserTrainingsResponseDTO());

        when(trainingRepository.findTrainerTrainings(
                eq("trainer1"),
                any(LocalDate.class),
                any(LocalDate.class),
                eq("trainee1")
        )).thenReturn(mockTrainings);

        when(trainingMapper.fromUserListToFetchUserTrainingsResponseList(mockTrainings)).thenReturn(responseDTOList);
    }

    @When("the findTrainerTrainings method is called")
    public void the_findTrainerTrainings_method_is_called() {
        result = trainingService.findTrainerTrainings(trainerTrainingsRequest);
    }

    @Then("a list of trainer trainings should be returned")
    public void a_list_of_trainer_trainings_should_be_returned() {
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trainerRepository, times(1)).findByUsername("trainer1");
        verify(trainingRepository, times(1)).findTrainerTrainings(
                anyString(), any(), any(), any()
        );
        verify(trainingMapper, times(1)).fromUserListToFetchUserTrainingsResponseList(mockTrainings);
    }
}
