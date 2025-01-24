package com.epam.spring.service;

import com.epam.spring.dto.request.trainee.CreateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.UpdateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.UpdateTraineeTrainerRequestDTO;
import com.epam.spring.dto.response.TrainingTypeDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainee.FetchTraineeResponseDTO;
import com.epam.spring.dto.response.trainee.UpdateTraineeResponseDTO;
import com.epam.spring.dto.response.trainer.TrainerResponseDTO;
import com.epam.spring.error.exception.UserNotFoundException;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Training;
import com.epam.spring.model.User;
import com.epam.spring.repository.TraineeRepository;
import com.epam.spring.repository.TrainerRepository;
import com.epam.spring.repository.UserRepository;
import com.epam.spring.util.PasswordGenerator;
import com.epam.spring.util.UsernameGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class TraineeService extends BaseUserService implements TraineeSpecificOperationsService {

    private final UsernameGenerator usernameGenerator;
    private final TraineeRepository traineeRepository;
    private final PasswordGenerator passwordGenerator;
    private final TrainerRepository trainerRepository;

    @Autowired
    public TraineeService(UserRepository userRepository, UsernameGenerator usernameGenerator, TraineeRepository traineeRepository, PasswordGenerator passwordGenerator, TrainerRepository trainerRepository) {
        super(userRepository);
        this.usernameGenerator = usernameGenerator;
        this.traineeRepository = traineeRepository;
        this.passwordGenerator = passwordGenerator;
        this.trainerRepository = trainerRepository;
    }

    @Override
    public UserCredentialsResponseDTO create(CreateTraineeRequestDTO createRequest) {
        String uniqueUsername = usernameGenerator.generateUniqueUsername(createRequest.getFirstName(), createRequest.getLastName());
        String password = passwordGenerator.generatePassword();

        Trainee trainee = new Trainee();
        User user = new User();
        user.setFirstName(createRequest.getFirstName());
        user.setLastName(createRequest.getLastName());
        user.setUsername(uniqueUsername);
        user.setPassword(password);
        user.setActive(false);
        trainee.setUser(user);
        trainee.setAddress(createRequest.getAddress());
        if (createRequest.getDateOfBirth() != null) {
            trainee.setDataOfBirth(LocalDate.parse(createRequest.getDateOfBirth()));
        }
        traineeRepository.create(trainee);

        return new UserCredentialsResponseDTO(uniqueUsername, password);
    }

    @Override
    public UpdateTraineeResponseDTO updateProfile(String username, UpdateTraineeRequestDTO updateRequest) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        User user = trainee.getUser();

        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setActive(updateRequest.getIsActive());
        trainee.setAddress(updateRequest.getAddress());
        trainee.setDataOfBirth(LocalDate.parse(updateRequest.getDateOfBirth()));

        Trainee updatedTrainee = traineeRepository.update(trainee);

        return UpdateTraineeResponseDTO.builder()
                .username(updatedTrainee.getUser().getUsername())
                .firstName(updatedTrainee.getUser().getFirstName())
                .lastName(updatedTrainee.getUser().getLastName())
                .dateOfBirth(String.valueOf(updatedTrainee.getDataOfBirth()))
                .isActive(updatedTrainee.getUser().isActive())
                .address(updatedTrainee.getAddress())
                .build();
    }

    @Override
    public FetchTraineeResponseDTO getUserProfile(String username) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        User user = trainee.getUser();
        List<Training> trainings = trainee.getTrainings();

        List<TrainerResponseDTO> trainers = trainings.stream().map(training -> new TrainerResponseDTO(
                training.getTrainer().getUser().getUsername(),
                training.getTrainer().getUser().getFirstName(),
                training.getTrainer().getUser().getLastName(),
                new TrainingTypeDTO(training.getTrainingType().getId(), training.getTrainingType().getTrainingTypeName())
        )).toList();

        return FetchTraineeResponseDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(String.valueOf(trainee.getDataOfBirth()))
                .address(trainee.getAddress())
                .isActive(trainee.getUser().isActive())
                .trainers(trainers)
                .build();
    }

    @Override
    public void deleteByUsername(String username) {
        traineeRepository.deleteByUsername(username);
    }

    @Override
    public TrainerResponseDTO updateTraineeTrainerList(String username, UpdateTraineeTrainerRequestDTO updateTraineeTrainerRequestDTO) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Trainee with username " + username + " not found"));
        List<String> trainerUsernames = updateTraineeTrainerRequestDTO.getTrainerUsernames();
        return null;
    }
}
