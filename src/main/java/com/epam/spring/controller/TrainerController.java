package com.epam.spring.controller;

import com.epam.spring.dto.request.ResourceIdentifierRequest;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/trainers", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @PostMapping
    public UserCredentialsResponseDTO register(@Valid @RequestBody CreateTrainerRequestDTO request) {
        return trainerService.create(request);
    }

    @GetMapping
    public FetchTrainerResponseDTO getTraineeProfile(@Valid @RequestBody ResourceIdentifierRequest request) {
        return trainerService.getUserProfile(request.getUsername());
    }

    @PutMapping("/{username}")
    public UpdateTrainerResponseDTO updateProfile(@PathVariable("username") String username, @Valid @RequestBody UpdateTrainerRequestDTO request) {
        return trainerService.updateProfile(username, request);
    }

    @PatchMapping("/activate")
    public void activateProfile(@Valid @RequestBody UserActivationRequestDTO request) {
        trainerService.activateProfile(request);
    }

    @PutMapping("/login/{username}/password")
    public void changeLogin(@PathVariable("username") String username, @Valid @RequestBody CredentialChangeRequestDTO request) {
        trainerService.changeCredentials(username, request);
    }

    @GetMapping("/login")
    public void login(@Valid @RequestBody UserCredentialsRequestDTO request) {
        trainerService.login(request);
    }

    @GetMapping("/{username}/trainings")
    public List<FetchUserTrainingsResponseDTO> getTrainerTraining(@PathVariable("username") String username, FetchTrainerTrainingsRequestDTO request) {
        return trainingService.findTrainerTrainings(username, request);
    }
}
