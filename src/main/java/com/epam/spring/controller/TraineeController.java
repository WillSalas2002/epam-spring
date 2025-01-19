package com.epam.spring.controller;

import com.epam.spring.dto.request.ResourceIdentifierRequest;
import com.epam.spring.dto.request.trainee.CreateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.UpdateTraineeRequestDTO;
import com.epam.spring.dto.request.user.UserActivationRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainee.FetchTraineeResponseDTO;
import com.epam.spring.dto.response.trainee.UpdateTraineeResponseDTO;
import com.epam.spring.service.TraineeService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/trainee", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class TraineeController {

    private final TraineeService traineeService;

    @PostMapping
    public UserCredentialsResponseDTO register(@Valid @RequestBody CreateTraineeRequestDTO request) {
        return traineeService.create(request);
    }

    @GetMapping
    public FetchTraineeResponseDTO getTraineeProfile(@Valid @RequestBody ResourceIdentifierRequest request) {
        return traineeService.getUserProfile(request.getUsername());
    }

    @PutMapping
    public UpdateTraineeResponseDTO updateProfile(@Valid @RequestBody UpdateTraineeRequestDTO request) {
        return traineeService.updateProfile(request);
    }

    @DeleteMapping
    public void delete(@Valid @RequestBody ResourceIdentifierRequest request) {
        traineeService.deleteByUsername(request.getUsername());
    }

    @PatchMapping
    public void activateProfile(@Valid @RequestBody UserActivationRequestDTO request) {
        traineeService.activateProfile(request);
    }
}
