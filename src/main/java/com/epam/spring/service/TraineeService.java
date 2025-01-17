package com.epam.spring.service;

import com.epam.spring.dto.request.UserActivationRequestDTO;
import com.epam.spring.dto.request.trainee.CreateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.UpdateTraineeRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainee.FetchTraineeResponseDTO;
import com.epam.spring.dto.response.trainee.UpdateTraineeResponseDTO;
import com.epam.spring.dto.response.trainer.TrainerResponseDTO;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Training;
import com.epam.spring.model.User;
import com.epam.spring.repository.TraineeRepository;
import com.epam.spring.util.PasswordGenerator;
import com.epam.spring.util.UsernameGenerator;
import com.epam.spring.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class TraineeService implements BaseUserOperationsService<CreateTraineeRequestDTO, UserCredentialsResponseDTO, FetchTraineeResponseDTO, UpdateTraineeRequestDTO, UpdateTraineeResponseDTO, UserActivationRequestDTO> {

    private final UsernameGenerator usernameGenerator;
    private final TraineeRepository traineeRepository;
    private final PasswordGenerator passwordGenerator;

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
        trainee.setDataOfBirth(LocalDate.parse(createRequest.getDateOfBirth()));

        traineeRepository.create(trainee);

        return new UserCredentialsResponseDTO(uniqueUsername, password);
    }

//    @Override
//    public List<Trainee> findAll() {
//        return traineeRepository.findAll();
//    }

//    @Override
//    public Trainee findById(Long id) {
//        Optional<Trainee> traineeOptional = traineeRepository.findById(id);
//        if (traineeOptional.isEmpty()) {
//            throw new RuntimeException("Trainee with id " + id + " not found");
//        }
//        return traineeOptional.get();
//    }

    @Override
    public UpdateTraineeResponseDTO updateProfile(UpdateTraineeRequestDTO updateRequest) {
        Optional<Trainee> traineeOptional = traineeRepository.findByUsername(updateRequest.getUsername());
        if (traineeOptional.isEmpty()) {
            throw new NoSuchElementException("Trainee with id " + updateRequest.getUsername() + " not found");
        }
        Trainee trainee = traineeOptional.get();
        User user = trainee.getUser();

        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setActive(Boolean.parseBoolean(updateRequest.getIsActive().getValue()));
        trainee.setAddress(updateRequest.getAddress());
        trainee.setDataOfBirth(LocalDate.parse(updateRequest.getDateOfBirth()));

        Trainee updatedTrainee = traineeRepository.update(trainee);

        return UpdateTraineeResponseDTO.builder()
                        .username(updatedTrainee.getUser().getUsername())
                        .firstName(updatedTrainee.getUser().getFirstName())
                        .lastName(updatedTrainee.getUser().getLastName())
                        .dateOfBirth(updatedTrainee.getDataOfBirth())
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
                training.getTrainingType().getTrainingTypeName()
        )).toList();

        return FetchTraineeResponseDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(trainee.getDataOfBirth())
                .address(trainee.getAddress())
                .isActive(trainee.getUser().isActive())
                .trainers(trainers)
                .build();
    }

//    @Override
    public boolean authenticate(String username, String password) {
        Optional<Trainee> traineeOptional = traineeRepository.findByUsername(username);

        return traineeOptional.filter(trainee -> Objects.equals(trainee.getUser().getPassword(), password)).isPresent();
    }

    @Override
    public void activateProfile(UserActivationRequestDTO activateRequest) {
        Optional<Trainee> traineeOptional = traineeRepository.findByUsername(activateRequest.getUsername());
        if (traineeOptional.isEmpty()) {
            throw new RuntimeException("Trainee with username " + activateRequest.getUsername() + " not found");
        }
        traineeOptional.get().getUser().setActive(Boolean.parseBoolean(activateRequest.getIsActive().getValue()));
        traineeRepository.update(traineeOptional.get());
    }

//    @Override
//    public void changePassword(String username, String oldPassword, String newPassword) {
//        Optional<Trainee> traineeOptional = traineeRepository.findByUsername(username);
//
//        if (traineeOptional.isEmpty()) {
//            throw new RuntimeException("Incorrect username");
//        }
//
//        Trainee trainee = traineeOptional.get();
//
//        if (!Objects.equals(trainee.getUser().getPassword(), oldPassword)) {
//            throw new RuntimeException("Incorrect password");
//        }
//        trainee.getUser().setPassword(newPassword);
//        traineeRepository.update(trainee);
//    }

//    @Override
//    public void delete(Trainee trainee) {
//        traineeRepository.delete(trainee);
//    }

//    @Override
    public void deleteByUsername(String username) {
        traineeRepository.deleteByUsername(username);
    }

    private boolean isNameChanged(Trainee trainee, Trainee updatedTrainee) {
        return !Objects.equals(trainee.getUser().getFirstName(), updatedTrainee.getUser().getFirstName()) ||
                !Objects.equals(trainee.getUser().getLastName(), updatedTrainee.getUser().getLastName());
    }
}
