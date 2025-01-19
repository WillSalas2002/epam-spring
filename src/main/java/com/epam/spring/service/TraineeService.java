package com.epam.spring.service;

import com.epam.spring.dto.TrainingTypeDTO;
import com.epam.spring.dto.request.trainee.CreateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.UpdateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.UpdateTraineeTrainerRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainee.FetchTraineeResponseDTO;
import com.epam.spring.dto.response.trainee.UpdateTraineeResponseDTO;
import com.epam.spring.dto.response.trainer.TrainerResponseDTO;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
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
import java.util.Optional;

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
        user.setActive(true);
        trainee.setUser(user);
        trainee.setAddress(createRequest.getAddress());
        if (createRequest.getDateOfBirth() != null) {
            trainee.setDataOfBirth(LocalDate.parse(createRequest.getDateOfBirth()));
        }
        traineeRepository.create(trainee);

        return new UserCredentialsResponseDTO(uniqueUsername, password);
    }

    @Override
    public UpdateTraineeResponseDTO updateProfile(UpdateTraineeRequestDTO updateRequest) {
        Optional<Trainee> traineeOptional = traineeRepository.findByUsername(updateRequest.getUsername());
        if (traineeOptional.isEmpty()) {
            throw new NoSuchElementException("Trainee with username " + updateRequest.getUsername() + " not found");
        }
        Trainee trainee = traineeOptional.get();
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
        Optional<Trainee> traineeOptional = traineeRepository.findByUsername(username);
        if (traineeOptional.isEmpty()) {
            throw new RuntimeException("Trainee with username " + username + " not found");
        }

        Trainee trainee = traineeOptional.get();
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

    // TODO: need to implement after clarifying with Daria
    @Override
    public TrainerResponseDTO updateTraineeTrainerList(UpdateTraineeTrainerRequestDTO updateTraineeTrainerRequestDTO) {
        String traineeUsername = updateTraineeTrainerRequestDTO.getTraineeUsername();
        Trainee trainee = traineeRepository.findByUsername(updateTraineeTrainerRequestDTO.getTraineeUsername())
                .orElseThrow(() -> new NoSuchElementException("Trainee with username " + traineeUsername + " not found"));
        List<String> trainerUsernames = updateTraineeTrainerRequestDTO.getTrainerUsernames();
        List<Trainer> trainers = trainerUsernames.stream()
                .map(username -> trainerRepository.findByUsername(username)
                        .orElseThrow(() -> new NoSuchElementException("Trainer with username " + username + " not found")))
                .toList();
        return null;
    }
}

/* TODO: 1. need to create separate mapper classes
/* TODO: 2. implement controllers
/* TODO: 3. error handling
/* TODO: 4. add swagger documentation
/* TODO: 5. JWT authorization
/* TODO: 6. improved logger
 */
