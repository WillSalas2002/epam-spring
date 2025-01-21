package com.epam.spring.service;

import com.epam.spring.config.TestConfig;
import com.epam.spring.dto.request.trainer.CreateTrainerRequestDTO;
import com.epam.spring.dto.request.trainer.UpdateTrainerRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainer.FetchTrainerResponseDTO;
import com.epam.spring.dto.response.trainer.UpdateTrainerResponseDTO;
import com.epam.spring.error.exception.UserNotFoundException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
class TrainerServiceTest {

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private SessionFactory sessionFactory;

    private CreateTrainerRequestDTO createTrainerRequestDTO;
    private final String firstName = "John";
    private final String lastName = "Doe";

    @BeforeEach
    void setUp() {
        createTrainerRequestDTO = buildCreateTrainerRequest(firstName, lastName);
    }

    @AfterEach
    void tearDown() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createMutationQuery("DELETE FROM Trainer").executeUpdate();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            transaction.commit();
        }
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
                .trainingTypeId(String.valueOf(1))
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
                .firstName(updatedFirstName)
                .lastName(updateLastName)
                .active(Boolean.TRUE)
                .specializationId(1L)
                .build();

        UpdateTrainerResponseDTO updateTrainerResponseDTO = trainerService.updateProfile(userCredentialsResponseDTO.getUsername(), updateRequest);

        assertEquals(updatedFirstName, updateTrainerResponseDTO.getFirstName());
        assertEquals(updateLastName, updateTrainerResponseDTO.getLastName());
        assertEquals(Boolean.TRUE, updateTrainerResponseDTO.getActive());
    }

    @Test
    void whenUpdateNonExistingTrainerThenThrowException() {
        String nonExistingUsername = "not exists";
        assertThrows(UserNotFoundException.class, () -> trainerService.updateProfile(nonExistingUsername, UpdateTrainerRequestDTO.builder().firstName(nonExistingUsername).build()), "User with username " + nonExistingUsername + " not found");
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