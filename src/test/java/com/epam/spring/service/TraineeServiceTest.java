package com.epam.spring.service;

import com.epam.spring.config.AppConfig;
import com.epam.spring.dto.request.trainee.CreateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.UpdateTraineeRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainee.FetchTraineeResponseDTO;
import com.epam.spring.dto.response.trainee.UpdateTraineeResponseDTO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppConfig.class})
@TestPropertySource("classpath:application-test.properties")
class TraineeServiceTest {

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
    void testCreate() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = traineeService.create(createTraineeRequestDTO);

        assertNotNull(userCredentialsResponseDTO);
        assertEquals(userCredentialsResponseDTO.getUsername(), FIRST_NAME + "." + LAST_NAME);
        assertEquals(10, userCredentialsResponseDTO.getPassword().length());
    }

    private static CreateTraineeRequestDTO buildCreateTraineeRequestDTO(String firstName, String lastName) {
        return CreateTraineeRequestDTO.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(LocalDate.now().minusYears(15).toString())
                .address("Street 77")
                .build();
    }

    @Test
    void testCreateWithExistingName() {
        UserCredentialsResponseDTO userCredentialsResponseDTO1 = traineeService.create(createTraineeRequestDTO);
        UserCredentialsResponseDTO userCredentialsResponseDTO2 = traineeService.create(createTraineeRequestDTO);

        assertEquals("John.Doe", userCredentialsResponseDTO1.getUsername());
        assertEquals("John.Doe.1", userCredentialsResponseDTO2.getUsername());
    }

    @Test
    void testUpdate() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = traineeService.create(createTraineeRequestDTO);
        String updateFirstName = "Will";
        String updatedLastName = "Salas";
        String updateDateOfBirth = "2002-06-03";

        UpdateTraineeRequestDTO updateTraineeRequestDTO = UpdateTraineeRequestDTO.builder()
                .firstName(updateFirstName)
                .lastName(updatedLastName)
                .isActive(Boolean.FALSE)
                .dateOfBirth(updateDateOfBirth)
                .build();

        UpdateTraineeResponseDTO updateTraineeResponseDTO = traineeService.updateProfile(userCredentialsResponseDTO.getUsername(),updateTraineeRequestDTO);

        assertEquals(updateFirstName, updateTraineeResponseDTO.getFirstName());
        assertEquals(updatedLastName, updateTraineeResponseDTO.getLastName());
        assertEquals(updateDateOfBirth, updateTraineeResponseDTO.getDateOfBirth());
    }

    @Test
    void whenUpdateNonExistingTraineeThenThrowException() {
        String username = "not exists";
        UpdateTraineeRequestDTO updateTraineeRequestDTO = UpdateTraineeRequestDTO.builder()
                .isActive(Boolean.FALSE)
                .build();
        assertThrows(NoSuchElementException.class, () -> traineeService.updateProfile(username, updateTraineeRequestDTO), "Trainee with username " + username + " not found");
    }

    @Test
    void testFindByUsername() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = traineeService.create(createTraineeRequestDTO);

        FetchTraineeResponseDTO userProfile = traineeService.getUserProfile(userCredentialsResponseDTO.getUsername());

        assertEquals(FIRST_NAME, userProfile.getFirstName());
        assertEquals(LAST_NAME, userProfile.getLastName());
        assertEquals(LocalDate.now().minusYears(15).toString(), userProfile.getDateOfBirth());
    }

    @Test
    void testDeleteByUsername() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = traineeService.create(createTraineeRequestDTO);
        String username = userCredentialsResponseDTO.getUsername();

        traineeService.deleteByUsername(username);

        assertThrows(RuntimeException.class, () -> traineeService.getUserProfile(username), "Trainer with username " + username + " not found");
    }
}