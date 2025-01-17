package com.epam.spring.service;

import com.epam.spring.config.AppConfig;
import com.epam.spring.dto.TrainingTypeDTO;
import com.epam.spring.dto.request.UserActivationRequestDTO;
import com.epam.spring.dto.request.trainer.CreateTrainerRequestDTO;
import com.epam.spring.dto.request.trainer.UpdateTrainerRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainer.FetchTrainerResponseDTO;
import com.epam.spring.dto.response.trainer.UpdateTrainerResponseDTO;
import com.epam.spring.model.TrainingType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(AppConfig.class)
class TrainerServiceTest {

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private SessionFactory sessionFactory;

    private final String firstName = "John";
    private final String lastName = "Doe";

    @BeforeEach
    void setUp() {
        buildCreateTrainerRequest(firstName, lastName);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createNativeMutationQuery("INSERT INTO training_types (training_type_name) VALUES ('Cardio')").executeUpdate();
            session.createNativeMutationQuery("ALTER TABLE training_types ALTER COLUMN id RESTART WITH 1").executeUpdate();
//            System.out.println("TrainingType: " + trainingType);
            transaction.commit();
        }
    }

    @AfterEach
    void tearDown() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createMutationQuery("DELETE FROM Trainer").executeUpdate();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.createMutationQuery("DELETE FROM TrainingType ").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    void testCreate() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = trainerService.create(buildCreateTrainerRequest(firstName, lastName));

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
        UserCredentialsResponseDTO userCredentialsResponseDTO1 = trainerService.create(buildCreateTrainerRequest(firstName, lastName));
        UserCredentialsResponseDTO userCredentialsResponseDTO2 = trainerService.create(buildCreateTrainerRequest(firstName, lastName));

        assertEquals("John.Doe", userCredentialsResponseDTO1.getUsername());
        assertEquals("John.Doe.1", userCredentialsResponseDTO2.getUsername());
    }

    @Test
    void testUpdate() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = trainerService.create(buildCreateTrainerRequest(firstName, lastName));
        String updatedFirstName = "Will";
        String updateLastName = "Salas";
        UpdateTrainerRequestDTO updateRequest = UpdateTrainerRequestDTO.builder()
                .username(firstName + "." + lastName)
                .firstName(updatedFirstName)
                .lastName(updateLastName)
                .active(Boolean.TRUE)
                .trainingType(new TrainingTypeDTO(1L, "Cardio"))
                .build();

        UpdateTrainerResponseDTO updateTrainerResponseDTO = trainerService.updateProfile(updateRequest);

        assertEquals(updatedFirstName, updateTrainerResponseDTO.getFirstName());
        assertEquals(updateLastName, updateTrainerResponseDTO.getLastName());
        assertEquals(Boolean.TRUE, updateTrainerResponseDTO.getActive());
    }

    @Test
    void whenUpdateNonExistingTrainerThenThrowException() {
        String nonExistingUsername = "not exists";
        assertThrows(NoSuchElementException.class, () -> trainerService.updateProfile(UpdateTrainerRequestDTO.builder().username(nonExistingUsername).build()), "Trainer with username " + nonExistingUsername + " not found");
    }

    @Test
    void testFindByUsername() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = trainerService.create(buildCreateTrainerRequest(firstName, lastName));

        FetchTrainerResponseDTO userProfile = trainerService.getUserProfile(userCredentialsResponseDTO.getUsername());

        assertNotNull(userProfile);
        assertEquals(firstName, userProfile.getFirstName());
        assertEquals(lastName, userProfile.getLastName());
    }

    // TODO: need to move this logic to User
    @Test
    void testActivateShouldNotBeIdempotent() {
        UserCredentialsResponseDTO userCredentialsResponseDTO = trainerService.create(buildCreateTrainerRequest(firstName, lastName));

        UserActivationRequestDTO activationRequest = new UserActivationRequestDTO(firstName + "." + lastName, Boolean.TRUE);
        trainerService.activateProfile(activationRequest);

        FetchTrainerResponseDTO userProfile = trainerService.getUserProfile(userCredentialsResponseDTO.getUsername());

        assertTrue(userProfile.getActive());
    }
}