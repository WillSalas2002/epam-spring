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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UserCredentialsResponseDTO> register(@Valid @RequestBody CreateTraineeRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(traineeService.create(request));
    }

    @GetMapping
    public ResponseEntity<FetchTraineeResponseDTO> getTraineeProfile(@RequestParam("username") String username) {
        return ResponseEntity.ok(traineeService.getUserProfile(username));
    }

    @PutMapping
    public ResponseEntity<UpdateTraineeResponseDTO> updateProfile(@RequestParam("username") String username, @Valid @RequestBody UpdateTraineeRequestDTO request) {
        return ResponseEntity.ok(traineeService.updateProfile(username, request));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam("username") String username) {
        traineeService.deleteByUsername(username);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/activate/status")
    public ResponseEntity<Void> activateProfile(@RequestParam("username") String username, @Valid @RequestBody UserActivationRequestDTO request) {
        traineeService.activateProfile(username, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/login/password")
    public ResponseEntity<Void> changeLogin(@RequestParam("username") String username, @Valid @RequestBody CredentialChangeRequestDTO request) {
        traineeService.changeCredentials(username, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestParam(value = "username") String username, @Valid @RequestBody UserCredentialsRequestDTO request) {
        traineeService.login(username, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unassigned-trainers")
    public ResponseEntity<List<TrainerResponseDTO>> findUnassignedTrainersByTraineeUsername(@RequestParam("username") String username) {
        return ResponseEntity.ok(trainerService.findUnassignedTrainersByTraineeUsername(username));
    }

    @GetMapping("/trainings")
    public ResponseEntity<List<FetchUserTrainingsResponseDTO>> getTraineeTrainings(@RequestParam("username") String username, FetchTraineeTrainingsRequestDTO request) {
        return ResponseEntity.ok(trainingService.findTraineeTrainings(username, request));
    }

    @PutMapping("/trainers")
    public ResponseEntity<TrainerResponseDTO> updateTraineeTrainers(@RequestParam("username") String username, UpdateTraineeTrainerRequestDTO updateTraineeTrainerRequestDTO) {
        return ResponseEntity.ok(traineeService.updateTraineeTrainerList(username, updateTraineeTrainerRequestDTO));
    }
}
