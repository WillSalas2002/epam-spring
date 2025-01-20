package com.epam.spring.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/trainees", produces = MediaType.APPLICATION_JSON_VALUE)
public class TraineeController {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @PostMapping
    public UserCredentialsResponseDTO register(@Valid @RequestBody CreateTraineeRequestDTO request) {
        return traineeService.create(request);
    }

    @GetMapping
    public FetchTraineeResponseDTO getTraineeProfile(@RequestParam("username") String username) {
        return traineeService.getUserProfile(username);
    }

    @PutMapping
    public UpdateTraineeResponseDTO updateProfile(@RequestParam("username") String username, @Valid @RequestBody UpdateTraineeRequestDTO request) {
        return traineeService.updateProfile(username, request);
    }

    @DeleteMapping
    public void delete(@RequestParam("username") String username) {
        traineeService.deleteByUsername(username);
    }

    @PatchMapping("/activate/status")
    public void activateProfile(@RequestParam("username") String username, @Valid @RequestBody UserActivationRequestDTO request) {
        traineeService.activateProfile(username, request);
    }

    @PutMapping("/login/password")
    public void changeLogin(@RequestParam("username") String username, @Valid @RequestBody CredentialChangeRequestDTO request) {
        traineeService.changeCredentials(username, request);
    }

    @GetMapping("/login")
    public void login(@RequestParam(value = "username") String username, @Valid @RequestBody UserCredentialsRequestDTO request) {
        traineeService.login(username, request);
    }

    @GetMapping("/unassigned-trainers")
    public List<TrainerResponseDTO> findUnassignedTrainersByTraineeUsername(@RequestParam("username") String username) {
        return trainerService.findUnassignedTrainersByTraineeUsername(username);
    }

    @GetMapping("/trainings")
    public List<FetchUserTrainingsResponseDTO> getTraineeTrainings(@RequestParam("username") String username, FetchTraineeTrainingsRequestDTO request) {
        return trainingService.findTraineeTrainings(username, request);
    }

    @PutMapping("/trainers")
    public TrainerResponseDTO updateTraineeTrainers(@RequestParam("username") String username, UpdateTraineeTrainerRequestDTO updateTraineeTrainerRequestDTO) {
        return traineeService.updateTraineeTrainerList(username, updateTraineeTrainerRequestDTO);
    }
}
