package com.epam.spring.service;

import com.epam.spring.dto.request.trainee.CreateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.UpdateTraineeRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainee.FetchTraineeResponseDTO;
import com.epam.spring.dto.response.trainee.UpdateTraineeResponseDTO;
import com.epam.spring.error.exception.ResourceNotFoundException;
import com.epam.spring.service.impl.TraineeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Rollback
@Transactional
class TraineeServiceTest {

    @Autowired
    private TraineeService traineeService;

    private final String FIRST_NAME = "John";
    private final String LAST_NAME = "Doe";
    private CreateTraineeRequestDTO createTraineeRequestDTO;

    @BeforeEach
    void setUp() {
        createTraineeRequestDTO = buildCreateTraineeRequestDTO(FIRST_NAME, LAST_NAME);
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
                .username(userCredentialsResponseDTO.getUsername())
                .firstName(updateFirstName)
                .lastName(updatedLastName)
                .isActive(Boolean.FALSE)
                .dateOfBirth(updateDateOfBirth)
                .build();

        UpdateTraineeResponseDTO updateTraineeResponseDTO = traineeService.updateProfile(updateTraineeRequestDTO);

        assertEquals(updateFirstName, updateTraineeResponseDTO.getFirstName());
        assertEquals(updatedLastName, updateTraineeResponseDTO.getLastName());
        assertEquals(updateDateOfBirth, updateTraineeResponseDTO.getDateOfBirth());
    }

    @Test
    void whenUpdateNonExistingTraineeThenThrowException() {
        UpdateTraineeRequestDTO updateTraineeRequestDTO = UpdateTraineeRequestDTO.builder()
                .isActive(Boolean.FALSE)
                .build();
        assertThrows(ResourceNotFoundException.class, () -> traineeService.updateProfile(updateTraineeRequestDTO));
    }

    @Test
    void testFindByUsername() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = traineeService.create(createTraineeRequestDTO);

        FetchTraineeResponseDTO userProfile = traineeService.getUserProfile(userCredentialsResponseDTO.getUsername());

        assertEquals(FIRST_NAME, userProfile.getFirstName());
        assertEquals(LAST_NAME, userProfile.getLastName());
        assertEquals(LocalDate.now().minusYears(15).toString(), userProfile.getDateOfBirth());
        assertNull(userProfile.getTrainers());
    }

    @Test
    void testDeleteByUsername() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = traineeService.create(createTraineeRequestDTO);
        String username = userCredentialsResponseDTO.getUsername();

        traineeService.deleteByUsername(username);

        assertThrows(ResourceNotFoundException.class, () -> traineeService.getUserProfile(username));
    }
}