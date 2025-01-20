package com.epam.spring.controller;

import com.epam.spring.dto.request.ResourceIdentifierRequest;
import com.epam.spring.dto.request.trainee.CreateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.UpdateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.UpdateTraineeTrainerRequestDTO;
import com.epam.spring.dto.request.training.FetchTraineeTrainingsRequestDTO;
import com.epam.spring.dto.request.user.CredentialChangeRequestDTO;
import com.epam.spring.dto.request.user.UserActivationRequestDTO;
import com.epam.spring.dto.request.user.UserCredentialsRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainee.FetchTraineeResponseDTO;
import com.epam.spring.dto.response.trainee.UpdateTraineeResponseDTO;
import com.epam.spring.dto.response.trainer.TrainerResponseDTO;
import com.epam.spring.dto.response.training.FetchUserTrainingsResponseDTO;
import com.epam.spring.service.TraineeService;
import com.epam.spring.service.TrainerService;
import com.epam.spring.service.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/trainees", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class TraineeController {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @PostMapping
    public UserCredentialsResponseDTO register(@Valid @RequestBody CreateTraineeRequestDTO request) {
        return traineeService.create(request);
    }

    @GetMapping("/{username}")
    public FetchTraineeResponseDTO getTraineeProfile(@PathVariable("username") String username) {
        return traineeService.getUserProfile(username);
    }

    @PutMapping("/{username}")
    public UpdateTraineeResponseDTO updateProfile(@PathVariable("username") String username, @Valid @RequestBody UpdateTraineeRequestDTO request) {
        return traineeService.updateProfile(username, request);
    }

    @DeleteMapping
    public void delete(@Valid @RequestBody ResourceIdentifierRequest request) {
        traineeService.deleteByUsername(request.getUsername());
    }

    @PatchMapping("/activate")
    public void activateProfile(@Valid @RequestBody UserActivationRequestDTO request) {
        traineeService.activateProfile(request);
    }

    @PutMapping("/login/{username}/password")
    public void changeLogin(@PathVariable("username") String username, @Valid @RequestBody CredentialChangeRequestDTO request) {
        traineeService.changeCredentials(username, request);
    }

    @GetMapping("/login")
    public void login(@Valid @RequestBody UserCredentialsRequestDTO request) {
        traineeService.login(request);
    }

    @GetMapping("/{username}/unassigned-trainers")
    public void findUnassignedTrainersByTraineeUsername(@PathVariable("username") String username) {
        trainerService.findUnassignedTrainersByTraineeUsername(username);
    }

    @GetMapping("/{username}/trainings")
    public List<FetchUserTrainingsResponseDTO> getTraineeTrainings(@PathVariable("username") String username, FetchTraineeTrainingsRequestDTO request) {
        return trainingService.findTraineeTrainings(username, request);
    }

    @PutMapping("/{username}/trainers")
    public TrainerResponseDTO updateTraineeTrainees(@PathVariable("username") String username, UpdateTraineeTrainerRequestDTO updateTraineeTrainerRequestDTO) {
        return traineeService.updateTraineeTrainerList(username, updateTraineeTrainerRequestDTO);
    }
}
