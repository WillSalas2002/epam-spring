package com.epam.spring.controller;

import com.epam.spring.dto.request.trainer.CreateTrainerRequestDTO;
import com.epam.spring.dto.request.trainer.UpdateTrainerRequestDTO;
import com.epam.spring.dto.request.training.FetchTrainerTrainingsRequestDTO;
import com.epam.spring.dto.request.user.CredentialChangeRequestDTO;
import com.epam.spring.dto.request.user.UserActivationRequestDTO;
import com.epam.spring.dto.request.user.UserCredentialsRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainer.FetchTrainerResponseDTO;
import com.epam.spring.dto.response.trainer.UpdateTrainerResponseDTO;
import com.epam.spring.dto.response.training.FetchUserTrainingsResponseDTO;
import com.epam.spring.service.TrainerService;
import com.epam.spring.service.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
@RequestMapping(value = "/api/v1/trainers", produces = MediaType.APPLICATION_JSON_VALUE)
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @PostMapping
    public UserCredentialsResponseDTO register(@Valid @RequestBody CreateTrainerRequestDTO request) {
        return trainerService.create(request);
    }

    @GetMapping
    public FetchTrainerResponseDTO getTraineeProfile(@RequestParam("username") String username) {
        return trainerService.getUserProfile(username);
    }

    @PutMapping
    public UpdateTrainerResponseDTO updateProfile(@RequestParam("username") String username, @Valid @RequestBody UpdateTrainerRequestDTO request) {
        return trainerService.updateProfile(username, request);
    }

    @PatchMapping("/activate/status")
    public void activateProfile(@RequestParam("username") String username, @Valid @RequestBody UserActivationRequestDTO request) {
        trainerService.activateProfile(username, request);
    }

    @PutMapping("/login/password")
    public void changeLogin(@RequestParam("username") String username, @Valid @RequestBody CredentialChangeRequestDTO request) {
        trainerService.changeCredentials(username, request);
    }

    @GetMapping("/login")
    public void login(@RequestParam("username") String username, @Valid @RequestBody UserCredentialsRequestDTO request) {
        trainerService.login(username, request);
    }

    @GetMapping("/trainings")
    public List<FetchUserTrainingsResponseDTO> getTrainerTraining(@RequestParam("username") String username, FetchTrainerTrainingsRequestDTO request) {
        return trainingService.findTrainerTrainings(username, request);
    }
}
