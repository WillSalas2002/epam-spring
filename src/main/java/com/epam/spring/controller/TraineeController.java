package com.epam.spring.controller;

import com.epam.spring.dto.request.trainee.CreateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.UpdateTraineeRequestDTO;
import com.epam.spring.dto.request.trainee.UpdateTraineeTrainerRequestDTO;
import com.epam.spring.dto.request.training.FetchTraineeTrainingsRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainee.FetchTraineeResponseDTO;
import com.epam.spring.dto.response.trainee.UpdateTraineeResponseDTO;
import com.epam.spring.dto.response.trainer.TrainerResponseDTO;
import com.epam.spring.dto.response.training.FetchUserTrainingsResponseDTO;
import com.epam.spring.service.impl.TraineeService;
import com.epam.spring.service.impl.TrainerService;
import com.epam.spring.service.impl.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/{username}")
    public ResponseEntity<FetchTraineeResponseDTO> getTraineeProfile(@PathVariable("username") String username) {
        return ResponseEntity.ok(traineeService.getUserProfile(username));
    }

    @PutMapping
    public ResponseEntity<UpdateTraineeResponseDTO> updateProfile(@Valid @RequestBody UpdateTraineeRequestDTO request) {
        return ResponseEntity.ok(traineeService.updateProfile(request));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> delete(@PathVariable("username") String username) {
        traineeService.deleteByUsername(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unassigned-trainers/{username}")
    public ResponseEntity<List<TrainerResponseDTO>> findUnassignedTrainersByTraineeUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(trainerService.findUnassignedTrainersByTraineeUsername(username));
    }

    @GetMapping("/trainings")
    public ResponseEntity<List<FetchUserTrainingsResponseDTO>> getTraineeTrainings(@Valid @RequestBody FetchTraineeTrainingsRequestDTO request) {
        return ResponseEntity.ok(trainingService.findTraineeTrainings(request));
    }

    @PutMapping("/trainers")
    public ResponseEntity<List<TrainerResponseDTO>> updateTraineeTrainers(@Valid @RequestBody UpdateTraineeTrainerRequestDTO updateTraineeTrainerRequestDTO) {
        return ResponseEntity.ok(traineeService.updateTraineeTrainerList(updateTraineeTrainerRequestDTO));
    }
}
