package com.epam.spring.service;

import com.epam.spring.dto.request.trainer.CreateTrainerRequestDTO;
import com.epam.spring.dto.request.trainer.UpdateTrainerRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainer.FetchTrainerResponseDTO;
import com.epam.spring.dto.response.trainer.UpdateTrainerResponseDTO;
import com.epam.spring.error.exception.ResourceNotFoundException;
import com.epam.spring.service.impl.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Rollback
@Transactional
class TrainerServiceTest {

    @Autowired
    private TrainerService trainerService;

    private CreateTrainerRequestDTO createTrainerRequestDTO;
    private final String firstName = "John";
    private final String lastName = "Doe";

    @BeforeEach
    void setUp() {
        createTrainerRequestDTO = buildCreateTrainerRequest(firstName, lastName);
    }

    @Test
    void testCreate() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = trainerService.create(createTrainerRequestDTO);

        assertNotNull(userCredentialsResponseDTO);
        assertEquals(firstName + "." + lastName, userCredentialsResponseDTO.getUsername());
        assertEquals(10, userCredentialsResponseDTO.getPassword().length());
    }

    private static CreateTrainerRequestDTO buildCreateTrainerRequest(String firstName, String lastName) {
        return CreateTrainerRequestDTO.builder()
                .firstName(firstName)
                .lastName(lastName)
                .trainingTypeId(1L)
                .build();
    }

    @Test
    void testCreateWithExistingName() {
        UserCredentialsResponseDTO userCredentialsResponseDTO1 = trainerService.create(createTrainerRequestDTO);
        UserCredentialsResponseDTO userCredentialsResponseDTO2 = trainerService.create(createTrainerRequestDTO);

        assertEquals("John.Doe", userCredentialsResponseDTO1.getUsername());
        assertEquals("John.Doe.1", userCredentialsResponseDTO2.getUsername());
    }

    @Test
    void testUpdate() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = trainerService.create(createTrainerRequestDTO);
        String updatedFirstName = "Will";
        String updateLastName = "Salas";
        UpdateTrainerRequestDTO updateRequest = UpdateTrainerRequestDTO.builder()
                .username(userCredentialsResponseDTO.getUsername())
                .firstName(updatedFirstName)
                .lastName(updateLastName)
                .active(Boolean.TRUE)
                .specializationId(String.valueOf(1L))
                .build();

        UpdateTrainerResponseDTO updateTrainerResponseDTO = trainerService.updateProfile(updateRequest);

        assertEquals(updatedFirstName, updateTrainerResponseDTO.getFirstName());
        assertEquals(updateLastName, updateTrainerResponseDTO.getLastName());
        assertEquals(Boolean.TRUE, updateTrainerResponseDTO.getActive());
        assertNull(updateTrainerResponseDTO.getTrainees());
    }

    @Test
    void whenUpdateNonExistingTrainerThenThrowException() {
        String nonExistingUsername = "not exists";
        assertThrows(ResourceNotFoundException.class, () -> trainerService.updateProfile(UpdateTrainerRequestDTO.builder().firstName(nonExistingUsername).build()));
    }

    @Test
    void testFindByUsername() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = trainerService.create(createTrainerRequestDTO);

        FetchTrainerResponseDTO userProfile = trainerService.getUserProfile(userCredentialsResponseDTO.getUsername());

        assertNotNull(userProfile);
        assertEquals(firstName, userProfile.getFirstName());
        assertEquals(lastName, userProfile.getLastName());
    }
}