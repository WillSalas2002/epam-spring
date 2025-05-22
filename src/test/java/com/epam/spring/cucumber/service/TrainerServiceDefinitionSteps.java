package com.epam.spring.cucumber.service;

import com.epam.spring.dto.request.trainer.CreateTrainerRequestDTO;
import com.epam.spring.dto.request.trainer.UpdateTrainerRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainer.FetchTrainerResponseDTO;
import com.epam.spring.dto.response.trainer.UpdateTrainerResponseDTO;
import com.epam.spring.error.exception.ResourceNotFoundException;
import com.epam.spring.mapper.TrainerMapper;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.TrainingType;
import com.epam.spring.model.User;
import com.epam.spring.repository.TrainerRepository;
import com.epam.spring.repository.TrainingTypeRepository;
import com.epam.spring.service.auth.JwtService;
import com.epam.spring.service.auth.TokenService;
import com.epam.spring.service.impl.TrainerService;
import com.epam.spring.util.PasswordGenerator;
import com.epam.spring.util.UsernameGenerator;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class TrainerServiceDefinitionSteps {

    private TrainerService trainerService;

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private TrainerMapper trainerMapper;
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

    private String username;
    private Exception thrownException;
    private CreateTrainerRequestDTO createRequest;
    private FetchTrainerResponseDTO fetchTrainerResponse;
    private UserCredentialsResponseDTO userCredentialsResponse;
    private AutoCloseable autoCloseable;
    private UpdateTrainerRequestDTO requestDTO;
    private UpdateTrainerResponseDTO updateTrainerResponse;

    private final Trainer mockTrainer = new Trainer();
    private final TrainingType mockTrainingType = new TrainingType();

    @Before
    public void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        trainerService = new TrainerService(
                usernameGenerator,
                tokenService,
                trainerRepository,
                null,
                trainingTypeRepository,
                trainerMapper,
                passwordGenerator,
                passwordEncoder,
                jwtService,
                new SimpleMeterRegistry()
        );
    }

    @Before
    public void cleanup() throws Exception {
        autoCloseable.close();
    }

    @Given("a valid create trainer request")
    public void a_valid_create_trainer_request() {
        createRequest = new CreateTrainerRequestDTO();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setTrainingTypeId(1L);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Yoga");

        when(trainingTypeRepository.findById(1L)).thenReturn(java.util.Optional.of(trainingType));
        when(usernameGenerator.generateUniqueUsername("John", "Doe")).thenReturn("john.doe123");
        when(passwordGenerator.generatePassword()).thenReturn("SecurePass1!");
        when(passwordEncoder.encode("SecurePass1!")).thenReturn("hashedPass");

        Trainer trainer = new Trainer();
        User user = new User();
        user.setUsername("john.doe123");
        trainer.setUser(user);
        trainer.setSpecialization(trainingType);

        when(trainerMapper.fromCreateTrainerRequestToTrainer(eq(createRequest), eq("john.doe123"), eq("hashedPass")))
                .thenReturn(trainer);
        when(trainerRepository.save(any())).thenReturn(trainer);
        when(jwtService.generateToken(any())).thenReturn("mocked.jwt.token");
    }

    @When("the trainer service creates the trainer")
    public void the_trainer_service_creates_the_trainer() {
        userCredentialsResponse = trainerService.create(createRequest);
    }

    @Then("the response should contain the generated username and password")
    public void the_response_should_contain_username_and_password() {
        assertEquals("john.doe123", userCredentialsResponse.getUsername());
        assertEquals("SecurePass1!", userCredentialsResponse.getPassword());
    }

    @Given("a trainer exists with username {string}")
    public void a_trainer_exists_with_username(String username) {
        this.username = username;
        mockTrainer.setUser(User.builder().username(username).build());
        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(mockTrainer));
        when(trainerMapper.fromTrainerToFetchTrainerResponse(mockTrainer))
                .thenReturn(new FetchTrainerResponseDTO());
    }

    @When("I fetch the trainer profile with username {string}")
    public void i_fetch_the_trainer_profile(String username) {
        try {
            fetchTrainerResponse = trainerService.getUserProfile(username);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the trainer profile should be returned successfully")
    public void the_trainer_profile_should_be_returned() {
        assertNotNull(fetchTrainerResponse);
    }

    @Given("no trainer exists with username {string}")
    public void no_trainer_exists_with_username(String username) {
        this.username = username;
        when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());
    }

    @Then("a ResourceNotFoundException should be thrown in trainer service")
    public void resource_not_found_exception_should_be_thrown() {
        assertInstanceOf(ResourceNotFoundException.class, thrownException);
    }

    @Given("a trainer exists with username {string} when update")
    public void a_trainer_exists_with_username_when_update(String username) {
        requestDTO = new UpdateTrainerRequestDTO();
        requestDTO.setUsername(username);
        requestDTO.setSpecializationId("101");

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(mockTrainer));
    }

    @Given("a training type exists with id {int}")
    public void a_training_type_exists_with_id(Integer id) {
        when(trainingTypeRepository.findById(Long.valueOf(id))).thenReturn(Optional.of(mockTrainingType));
    }

    @When("I update the trainer profile with valid data")
    public void i_update_the_trainer_profile_with_valid_data() {
        try {
            when(trainerRepository.save(mockTrainer)).thenReturn(mockTrainer);
            when(trainerMapper.fromTrainerToUpdatedTrainerResponse(mockTrainer))
                    .thenReturn(new UpdateTrainerResponseDTO());

            updateTrainerResponse = trainerService.updateProfile(requestDTO);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the trainer profile should be updated successfully")
    public void the_trainer_profile_should_be_updated_successfully() {
        assertNotNull(updateTrainerResponse);
    }

    @Given("no trainer exists with username {string} when update")
    public void no_trainer_exists_with_username_when_update(String username) {
        requestDTO = new UpdateTrainerRequestDTO();
        requestDTO.setUsername(username);
        requestDTO.setSpecializationId("101");

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());
    }

    @When("I update the trainer profile with that username")
    @When("I update the trainer profile with that training type id")
    public void i_update_the_trainer_profile_with_invalid_data() {
        try {
            trainerService.updateProfile(requestDTO);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Given("no training type exists with id {int}")
    public void no_training_type_exists_with_id(Integer id) {
        requestDTO = new UpdateTrainerRequestDTO();
        requestDTO.setUsername("trainer2");
        requestDTO.setSpecializationId(id.toString());

        when(trainerRepository.findByUsername("trainer2")).thenReturn(Optional.of(mockTrainer));
        when(trainingTypeRepository.findById(Long.valueOf(id))).thenReturn(Optional.empty());
    }
}
