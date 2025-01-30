package com.epam.spring.service;

import com.epam.spring.config.TestConfig;
import com.epam.spring.dto.request.trainee.CreateTraineeRequestDTO;
import com.epam.spring.dto.request.user.CredentialChangeRequestDTO;
import com.epam.spring.dto.request.user.UserCredentialsRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainee.FetchTraineeResponseDTO;
import com.epam.spring.error.exception.IncorrectCredentialsException;
import com.epam.spring.service.impl.TraineeService;
import com.epam.spring.service.impl.UserService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private SessionFactory sessionFactory;

    private final String FIRST_NAME = "John";
    private final String LAST_NAME = "Doe";
    private CreateTraineeRequestDTO createTraineeRequestDTO;

    @BeforeEach
    void setUp() {
        createTraineeRequestDTO = buildCreateTraineeRequestDTO(FIRST_NAME, LAST_NAME);
    }


    @AfterEach
    void tearDown() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createMutationQuery("DELETE FROM Trainee").executeUpdate();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    public void testChangeCredentials() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = traineeService.create(createTraineeRequestDTO);
        CredentialChangeRequestDTO credentialChangeRequest = new CredentialChangeRequestDTO(
                userCredentialsResponseDTO.getUsername(),
                userCredentialsResponseDTO.getPassword(),
                "1111111111");

        UserCredentialsResponseDTO userCredentialsResponse = userService.changeCredentials(credentialChangeRequest);

        assertEquals("1111111111", userCredentialsResponse.getPassword());
    }

    @Test
    public void testChangeCredentialsWithIncorrectOldPassword() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = traineeService.create(createTraineeRequestDTO);
        CredentialChangeRequestDTO credentialChangeRequest = new CredentialChangeRequestDTO(
                userCredentialsResponseDTO.getUsername(),
                "incorrect password",
                "1111111111");

        assertThrows(IncorrectCredentialsException.class, () -> userService.changeCredentials(credentialChangeRequest), "Incorrect old password");
    }

    @Test
    public void testLoginShouldReturnTrueWhenCredentialsCorrect() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = traineeService.create(createTraineeRequestDTO);
        UserCredentialsRequestDTO userCredentialsRequest = new UserCredentialsRequestDTO(
                userCredentialsResponseDTO.getUsername(),
                userCredentialsResponseDTO.getPassword());

        assertDoesNotThrow(() -> userService.login(userCredentialsRequest));
    }

    @Test
    public void testLoginShouldReturnFalseWhenCredentialsCorrect() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = traineeService.create(createTraineeRequestDTO);

        UserCredentialsRequestDTO userCredentialsRequest = new UserCredentialsRequestDTO(
                userCredentialsResponseDTO.getUsername(),
                "incorrect password");

        assertThrows(IncorrectCredentialsException.class, () -> userService.login(userCredentialsRequest));
    }

    @Test
    void testActivateProfile() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = traineeService.create(createTraineeRequestDTO);

        userService.activateProfile(userCredentialsResponseDTO.getUsername());
        FetchTraineeResponseDTO userProfile = traineeService.getUserProfile(userCredentialsResponseDTO.getUsername());

        assertTrue(userProfile.isActive());
    }

    @Test
    void testAuthenticate() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = traineeService.create(createTraineeRequestDTO);

        assertDoesNotThrow(() -> userService.authenticate(userCredentialsResponseDTO.getUsername(), userCredentialsResponseDTO.getPassword()));
    }

    private static CreateTraineeRequestDTO buildCreateTraineeRequestDTO(String firstName, String lastName) {
        return CreateTraineeRequestDTO.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(LocalDate.now().minusYears(15).toString())
                .address("Street 77")
                .build();
    }
}