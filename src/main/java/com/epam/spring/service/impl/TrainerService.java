package com.epam.spring.service.impl;

import com.epam.spring.dto.request.trainer.CreateTrainerRequestDTO;
import com.epam.spring.dto.request.trainer.UpdateTrainerRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainer.FetchTrainerResponseDTO;
import com.epam.spring.dto.response.trainer.TrainerResponseDTO;
import com.epam.spring.dto.response.trainer.UpdateTrainerResponseDTO;
import com.epam.spring.error.exception.UserNotFoundException;
import com.epam.spring.mapper.TrainerMapper;
import com.epam.spring.model.Trainer;
import com.epam.spring.repository.impl.TrainerRepository;
import com.epam.spring.service.base.TrainerSpecificOperationsService;
import com.epam.spring.util.PasswordGenerator;
import com.epam.spring.util.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerService implements TrainerSpecificOperationsService {

    private final UsernameGenerator usernameGenerator;
    private final TrainerRepository trainerRepository;
    private final PasswordGenerator passwordGenerator;
    private final TrainerMapper trainerMapper;

    @Override
    public UserCredentialsResponseDTO create(CreateTrainerRequestDTO createRequestDTO) {
        String uniqueUsername = usernameGenerator.generateUniqueUsername(createRequestDTO.getFirstName(), createRequestDTO.getLastName());
        String password = passwordGenerator.generatePassword();
        Trainer trainer = trainerMapper.fromCreateTrainerRequestToTrainer(createRequestDTO, uniqueUsername, password);
        Trainer createTrainer = trainerRepository.create(trainer);
        return new UserCredentialsResponseDTO(createTrainer.getUser().getUsername(), createTrainer.getUser().getPassword());
    }

    @Override
    public FetchTrainerResponseDTO getUserProfile(String username) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return trainerMapper.fromTrainerToFetchTrainerResponse(trainer);
    }

    @Override
    public UpdateTrainerResponseDTO updateProfile(UpdateTrainerRequestDTO updateRequestDto) {
        Trainer trainer = trainerRepository.findByUsername(updateRequestDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException(updateRequestDto.getUsername()));
        trainerMapper.fromUpdateTrainerRequestToTrainer(trainer, updateRequestDto);
        Trainer updatedTrainer = trainerRepository.update(trainer);
        return trainerMapper.fromTrainerToUpdatedTrainerResponse(updatedTrainer);
    }

    @Override
    public List<TrainerResponseDTO> findUnassignedTrainersByTraineeUsername(String username) {
        List<Trainer> trainers = trainerRepository.findUnassignedTrainersByTraineeUsername(username);
        return trainerMapper.fromTrainerListToTrainerResponseDTOList(trainers);
    }
}
