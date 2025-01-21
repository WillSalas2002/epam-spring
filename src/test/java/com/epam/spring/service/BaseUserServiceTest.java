package com.epam.spring.service;

import com.epam.spring.config.AppConfig;
import com.epam.spring.dto.request.trainee.CreateTraineeRequestDTO;
import com.epam.spring.dto.request.user.CredentialChangeRequestDTO;
import com.epam.spring.dto.request.user.UserActivationRequestDTO;
import com.epam.spring.dto.request.user.UserCredentialsRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainee.FetchTraineeResponseDTO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppConfig.class})
@TestPropertySource("classpath:application-test.properties")
class BaseUserServiceTest {

    @Qualifier("traineeService")
    @Autowired
    private TraineeService baseUserService;

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
        UserCredentialsResponseDTO userCredentialsResponseDTO = baseUserService.create(createTraineeRequestDTO);
        CredentialChangeRequestDTO credentialChangeRequest = new CredentialChangeRequestDTO(
                userCredentialsResponseDTO.getPassword(),
                "1111111111");

        UserCredentialsResponseDTO userCredentialsResponse = baseUserService.changeCredentials(userCredentialsResponseDTO.getUsername(), credentialChangeRequest);

        assertEquals("1111111111", userCredentialsResponse.getPassword());
    }

    @Test
    public void testLoginShouldReturnTrueWhenCredentialsCorrect() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = baseUserService.create(createTraineeRequestDTO);
        UserCredentialsRequestDTO userCredentialsRequest = new UserCredentialsRequestDTO(
                userCredentialsResponseDTO.getPassword());
        boolean login = baseUserService.login(userCredentialsResponseDTO.getUsername(), userCredentialsRequest);

        assertTrue(login);
    }

    @Test
    public void testLoginShouldReturnFalseWhenCredentialsCorrect() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = baseUserService.create(createTraineeRequestDTO);

        UserCredentialsRequestDTO userCredentialsRequest = new UserCredentialsRequestDTO(
                "incorrect password");

        boolean login = baseUserService.login(userCredentialsResponseDTO.getUsername(), userCredentialsRequest);

        assertFalse(login);
    }

    @Test
    void testActivateProfile() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = baseUserService.create(createTraineeRequestDTO);
        UserActivationRequestDTO userActivationRequestDTO = new UserActivationRequestDTO(Boolean.TRUE);

        baseUserService.activateProfile(userCredentialsResponseDTO.getUsername(), userActivationRequestDTO);
        FetchTraineeResponseDTO userProfile = baseUserService.getUserProfile(userCredentialsResponseDTO.getUsername());

        assertTrue(userProfile.isActive());
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