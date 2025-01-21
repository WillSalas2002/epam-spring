package com.epam.spring.service;

import com.epam.spring.dto.request.trainer.CreateTrainerRequestDTO;
import com.epam.spring.dto.request.trainer.UpdateTrainerRequestDTO;
import com.epam.spring.dto.response.TrainingTypeDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainee.TraineeResponseDTO;
import com.epam.spring.dto.response.trainer.FetchTrainerResponseDTO;
import com.epam.spring.dto.response.trainer.TrainerResponseDTO;
import com.epam.spring.dto.response.trainer.UpdateTrainerResponseDTO;
import com.epam.spring.error.exception.UserNotFoundException;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import com.epam.spring.model.User;
import com.epam.spring.repository.TrainerRepository;
import com.epam.spring.repository.UserRepository;
import com.epam.spring.util.PasswordGenerator;
import com.epam.spring.util.UsernameGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TrainerService extends BaseUserService implements TrainerSpecificOperationsService {

    private final UsernameGenerator usernameGenerator;
    private final TrainerRepository trainerRepository;
    private final PasswordGenerator passwordGenerator;

    @Autowired
    public TrainerService(UserRepository userRepository, UsernameGenerator usernameGenerator, TrainerRepository trainerRepository, PasswordGenerator passwordGenerator) {
        super(userRepository);
        this.usernameGenerator = usernameGenerator;
        this.trainerRepository = trainerRepository;
        this.passwordGenerator = passwordGenerator;
    }

    @Override
    public UserCredentialsResponseDTO create(CreateTrainerRequestDTO createRequestDTO) {
        String uniqueUsername = usernameGenerator.generateUniqueUsername(createRequestDTO.getFirstName(), createRequestDTO.getLastName());
        String password = passwordGenerator.generatePassword();

        User user = User.builder()
                .firstName(createRequestDTO.getFirstName())
                .lastName(createRequestDTO.getLastName())
                .username(uniqueUsername)
                .password(password)
                .isActive(Boolean.FALSE)
                .build();
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(TrainingType.builder().id(Long.valueOf(createRequestDTO.getTrainingTypeId())).build());

        Trainer createTrainer = trainerRepository.create(trainer);

        return new UserCredentialsResponseDTO(createTrainer.getUser().getUsername(), createTrainer.getUser().getPassword());
    }

    @Override
    public FetchTrainerResponseDTO getUserProfile(String username) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        User user = trainer.getUser();
        List<Training> trainings = trainer.getTrainings();

        List<TraineeResponseDTO> trainees = trainings.stream().map(training -> new TraineeResponseDTO(
                training.getTrainee().getUser().getUsername(),
                training.getTrainee().getUser().getFirstName(),
                training.getTrainee().getUser().getLastName()
        )).toList();

        return FetchTrainerResponseDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .active(trainer.getUser().isActive())
                .specializationId(trainer.getSpecialization().getId())
                .trainees(trainees)
                .build();
    }

    @Override
    public UpdateTrainerResponseDTO updateProfile(String username, UpdateTrainerRequestDTO updateRequestDto) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        User user = trainer.getUser();
        user.setFirstName(updateRequestDto.getFirstName());
        user.setLastName(updateRequestDto.getLastName());
        user.setActive(updateRequestDto.getActive());

        TrainingType specialization = new TrainingType();
        specialization.setId(updateRequestDto.getSpecializationId());
        trainer.setSpecialization(specialization);

        Trainer updatedTrainer = trainerRepository.update(trainer);

        List<TraineeResponseDTO> traineeDTOList = updatedTrainer.getTrainings().stream()
                .map(training -> new TraineeResponseDTO(
                        training.getTrainee().getUser().getUsername(),
                        training.getTrainee().getUser().getFirstName(),
                        training.getTrainee().getUser().getFirstName()
                )).toList();

        return UpdateTrainerResponseDTO.builder()
                .firstName(updatedTrainer.getUser().getFirstName())
                .lastName(updatedTrainer.getUser().getLastName())
                .username(updatedTrainer.getUser().getUsername())
                .specialization(new TrainingTypeDTO(updatedTrainer.getSpecialization().getId(), updatedTrainer.getSpecialization().getTrainingTypeName()))
                .active(updateRequestDto.getActive())
                .trainees(traineeDTOList)
                .build();
    }

    @Override
    public List<TrainerResponseDTO> findUnassignedTrainersByTraineeUsername(String username) {
        return trainerRepository.findUnassignedTrainersByTraineeUsername(username).stream()
                .map(trainer -> new TrainerResponseDTO(
                        trainer.getUser().getUsername(),
                        trainer.getUser().getFirstName(),
                        trainer.getUser().getLastName(),
                        new TrainingTypeDTO(
                                trainer.getSpecialization().getId(),
                                trainer.getSpecialization().getTrainingTypeName()
                        )
                )).toList();
    }
}
