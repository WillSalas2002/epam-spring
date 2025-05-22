package com.epam.spring.cucumber.service;

import com.epam.spring.dto.request.trainee.CreateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.TrainingIdTrainerUsernamePair;
import com.epam.spring.dto.request.trainee.UpdateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.UpdateTraineeTrainerRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainee.FetchTraineeResponseDTO;
import com.epam.spring.dto.response.trainee.UpdateTraineeResponseDTO;
import com.epam.spring.dto.response.trainer.TrainerResponseDTO;
import com.epam.spring.error.exception.ResourceNotFoundException;
import com.epam.spring.mapper.TraineeMapper;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import com.epam.spring.model.User;
import com.epam.spring.repository.TraineeRepository;
import com.epam.spring.repository.TrainerRepository;
import com.epam.spring.repository.UserRepository;
import com.epam.spring.service.auth.JwtService;
import com.epam.spring.service.auth.MyUserPrincipal;
import com.epam.spring.service.auth.TokenService;
import com.epam.spring.service.impl.TraineeService;
import com.epam.spring.util.PasswordGenerator;
import com.epam.spring.util.UsernameGenerator;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TraineeServiceDefinitionSteps {

    @InjectMocks
    private TraineeService traineeService;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private UsernameGenerator usernameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private JwtService jwtService;
    @Mock
    private TokenService tokenService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TraineeMapper traineeMapper;

    private CreateTraineeRequestDTO createRequest;
    private UpdateTraineeRequestDTO updateRequest;
    private UserCredentialsResponseDTO credentialsResponse;
    private FetchTraineeResponseDTO fetchResponse;
    private String usernameToDelete;
    private Trainee traineeToDelete;
    private User traineeUser;
    private Trainee existingTrainee;
    private User user;
    private Exception exception;
    private UpdateTraineeResponseDTO updateResponse;
    private Trainee trainee;
    private Training training;
    private Trainer trainer;
    private UpdateTraineeTrainerRequestDTO updateTraineeTrainerRequest;
    private List<TrainerResponseDTO> response;

    private AutoCloseable autoCloseable;

    @Before
    public void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @Before
    public void cleanup() throws Exception {
        autoCloseable.close();
    }

    @Given("a create trainee request with first name {string} and last name {string}")
    public void a_create_trainee_request(String firstName, String lastName) {
        createRequest = new CreateTraineeRequestDTO();
        createRequest.setFirstName(firstName);
        createRequest.setLastName(lastName);

        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .username("John.Doe")
                .password("1234567890")
                .isActive(true)
                .build();

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(usernameGenerator.generateUniqueUsername(firstName, lastName)).thenReturn("John.Doe");
        when(passwordGenerator.generatePassword()).thenReturn("pass123");
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(traineeMapper.fromCreateTraineeRequestToTrainee(any(), Mockito.eq("John.Doe"), Mockito.eq("encoded")))
                .thenReturn(trainee);
        when(traineeRepository.save(any())).thenReturn(trainee);
        when(jwtService.generateToken(any(MyUserPrincipal.class))).thenReturn("token");
        doNothing().when(tokenService).updateUserToken(anyString(), anyString());
    }

    @When("the service creates the trainee")
    public void the_service_creates_the_trainee() {
        credentialsResponse = traineeService.create(createRequest);
    }

    @Then("the response should contain a non-null username and password")
    public void the_response_should_be_valid() {
        assertNotNull(credentialsResponse.getUsername());
        assertNotNull(credentialsResponse.getPassword());
    }

    @Given("a trainee with username {string}, firstname {string} and lastname {string} exists")
    public void a_trainee_with_username_exists(String username, String firstname, String lastname) {
        Trainee trainee = new Trainee();
        trainee.setUser(User.builder().username(username).build());

        fetchResponse = new FetchTraineeResponseDTO();
        fetchResponse.setFirstName(firstname);
        fetchResponse.setLastName(lastname);

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(traineeMapper.fromTraineeToFetchTraineeResponse(trainee)).thenReturn(fetchResponse);
    }

    @When("the service fetches the profile for username {string}")
    public void the_service_fetches_the_profile(String username) {
        fetchResponse = traineeService.getUserProfile(username);
    }

    @Then("the response should contain the correct trainee information")
    public void the_response_should_contain_correct_trainee_info() {
        assertNotNull(fetchResponse);
        assertEquals("John", fetchResponse.getFirstName());
        assertEquals("Doe", fetchResponse.getLastName());
    }

    @Given("no trainee exists with username {string}")
    public void no_trainee_exists(String username) {
        updateRequest = new UpdateTraineeRequestDTO();
        updateRequest.setUsername(username);
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());
    }

    @When("the service tries to update the trainee with username {string}")
    public void the_service_tries_to_update() {
        traineeService.updateProfile(updateRequest);
    }

    @Given("a trainee with username {string} exists in the system")
    public void trainee_exists_in_system(String username) {
        usernameToDelete = username;

        traineeUser = new User();
        traineeUser.setUsername(username);

        traineeToDelete = new Trainee();
        traineeToDelete.setUser(traineeUser);

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(traineeToDelete));
    }

    @When("the service deletes the trainee with username {string}")
    public void service_deletes_trainee(String username) {
        // Make sendDeleteMessageToQueue a do-nothing if you want to skip its logic
        doNothing().when(trainerRepository).delete(any()); // Optional

        traineeService.deleteByUsername(username);
    }

    @Then("the trainee and related user should be removed from the repository")
    public void trainee_and_user_should_be_removed() {
        verify(traineeRepository, times(1)).findByUsername(usernameToDelete);
        verify(traineeRepository, times(1)).delete(traineeToDelete);
        verify(userRepository, times(1)).delete(traineeUser);
    }

    @Given("a trainee with username {string} exists")
    public void a_trainee_with_username_exists(String username) {
        user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .username("John.Doe")
                .password("1234567890")
                .isActive(true)
                .build();

        existingTrainee = new Trainee();
        existingTrainee.setUser(user);

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(existingTrainee));
    }

    @And("the update request has new first name {string} and last name {string}")
    public void the_update_request_has_new_data(String firstName, String lastName) {
        updateRequest = new UpdateTraineeRequestDTO();
        updateRequest.setUsername(existingTrainee.getUser().getUsername());
        updateRequest.setFirstName(firstName);
        updateRequest.setLastName(lastName);

        // Mock mapper behavior
        doNothing().when(traineeMapper).fromUpdateTraineeRequestToTrainee(existingTrainee, updateRequest);

        Trainee updatedTrainee = new Trainee();
        updatedTrainee.setUser(user);
        when(traineeRepository.save(existingTrainee)).thenReturn(updatedTrainee);

        UpdateTraineeResponseDTO responseDTO = new UpdateTraineeResponseDTO();
        responseDTO.setUsername(existingTrainee.getUser().getUsername());
        responseDTO.setFirstName(firstName);
        responseDTO.setLastName(lastName);
        when(traineeMapper.fromTraineeToUpdateTraineeResponse(updatedTrainee)).thenReturn(responseDTO);
    }

    @When("the service updates the trainee profile")
    public void the_service_updates_the_trainee_profile() {
        updateResponse = traineeService.updateProfile(updateRequest);
    }

    @Then("the trainee should be saved with updated data")
    public void the_trainee_should_be_saved() {
        verify(traineeRepository).save(existingTrainee);
        verify(traineeMapper).fromUpdateTraineeRequestToTrainee(existingTrainee, updateRequest);
    }

    @And("the update response should contain the updated data")
    public void the_update_response_should_contain_the_updated_data() {
        assertNotNull(updateResponse);
        assertEquals(updateRequest.getUsername(), updateResponse.getUsername());
        assertEquals(updateRequest.getFirstName(), updateResponse.getFirstName());
        assertEquals(updateRequest.getLastName(), updateResponse.getLastName());
    }

    @Given("no trainee with username {string} exists")
    public void no_trainee_with_username_exists(String username) {
        updateRequest = new UpdateTraineeRequestDTO();
        updateRequest.setUsername(username);
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());
    }

    @When("the service tries to update the profile of unknown trainee")
    public void the_service_tries_to_update_profile() {
        try {
            traineeService.updateProfile(updateRequest);
        } catch (Exception e) {
            this.exception = e;
        }
    }

    @Given("a trainee with username {string} exists and has training with ID {long}")
    public void trainee_with_training_exists(String username, long trainingId) {
        User user = User.builder()
                .username(username)
                .build();
        trainee = new Trainee();
        trainee.setUser(user);

        training = new Training();
        training.setId(trainingId);

        trainee.setTrainings(List.of(training));
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
    }

    @And("a trainer with username {string} exists and has specialization {string}")
    public void trainer_exists(String trainerUsername, String specializationName) {
        User trainerUser = new User();
        trainerUser.setUsername(trainerUsername);
        trainerUser.setFirstName("TrainerFirst");
        trainerUser.setLastName("TrainerLast");

        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName(specializationName);

        trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(trainingType);

        when(trainerRepository.findByUsername(trainerUsername)).thenReturn(Optional.of(trainer));
    }

    @And("the update request maps training {long} to trainer {string}")
    public void map_training_to_trainer(long trainingId, String trainerUsername) {
        TrainingIdTrainerUsernamePair pair = new TrainingIdTrainerUsernamePair();
        pair.setTrainingId(trainingId);
        pair.setTrainerUsername(trainerUsername);

        updateTraineeTrainerRequest = new UpdateTraineeTrainerRequestDTO();
        updateTraineeTrainerRequest.setTraineeUsername(trainee.getUser().getUsername());
        updateTraineeTrainerRequest.setTrainingIdTrainerUsernamePairs(List.of(pair));

        when(traineeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @When("the service updates the trainee trainer list")
    public void service_updates_trainer_list() {
        try {
            response = traineeService.updateTraineeTrainerList(updateTraineeTrainerRequest);
        } catch (Exception e) {
            this.exception = e;
        }
    }

    @Then("the trainee should be saved with updated trainer info")
    public void trainee_saved_with_trainer() {
        verify(traineeRepository).save(trainee);
        assertEquals(trainer, training.getTrainer());
        assertEquals(trainer.getSpecialization(), training.getTrainingType());
    }

    @And("the response should include trainer {string} with specialization {string}")
    public void response_should_include_trainer(String trainerUsername, String specialization) {
        assertNotNull(response);
        assertEquals(1, response.size());
        TrainerResponseDTO dto = response.get(0);
        assertEquals(trainerUsername, dto.getUsername());
        assertEquals(specialization, dto.getSpecialization().getTrainingTypeName());
    }

    @Given("no trainee with username {string} exists when update list")
    public void no_trainee_with_username_exists_when_updating_list(String username) {
        updateTraineeTrainerRequest = new UpdateTraineeTrainerRequestDTO();
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());
    }

    @When("the service tries to update trainer list for unknown trainee")
    public void service_tries_to_update() {
        try {
            traineeService.updateTraineeTrainerList(updateTraineeTrainerRequest);
        } catch (Exception e) {
            this.exception = e;
        }
    }

    @Then("a ResourceNotFoundException should be thrown")
    public void resource_not_found_thrown() {
        assertNotNull(exception);
        assertInstanceOf(ResourceNotFoundException.class, exception);
    }

    @And("no trainer with username {string} exists")
    public void no_trainer_exists(String trainerUsername) {
        when(trainerRepository.findByUsername(trainerUsername)).thenReturn(Optional.empty());
    }

    @Then("a NoSuchElementException should be thrown")
    public void no_such_element_exception_thrown() {
        assertNotNull(exception);
        assertInstanceOf(NoSuchElementException.class, exception);
    }
}
