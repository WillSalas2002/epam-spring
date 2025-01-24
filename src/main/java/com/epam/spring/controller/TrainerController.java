package com.epam.spring.controller;

import com.epam.spring.dto.request.trainer.CreateTrainerRequestDTO;
import com.epam.spring.dto.request.trainer.UpdateTrainerRequestDTO;
import com.epam.spring.dto.request.training.FetchTrainerTrainingsRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.dto.response.trainer.FetchTrainerResponseDTO;
import com.epam.spring.dto.response.trainer.UpdateTrainerResponseDTO;
import com.epam.spring.dto.response.training.FetchUserTrainingsResponseDTO;
import com.epam.spring.service.impl.TrainerService;
import com.epam.spring.service.impl.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<UserCredentialsResponseDTO> register(@Valid @RequestBody CreateTrainerRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainerService.create(request));
    }

    @GetMapping
    public ResponseEntity<FetchTrainerResponseDTO> getTraineeProfile(@RequestParam("username") String username) {
        return ResponseEntity.ok(trainerService.getUserProfile(username));
    }

    @PutMapping
    public ResponseEntity<UpdateTrainerResponseDTO> updateProfile(@RequestParam("username") String username, @Valid @RequestBody UpdateTrainerRequestDTO request) {
        return ResponseEntity.ok(trainerService.updateProfile(username, request));
    }

    @GetMapping("/trainings")
    public ResponseEntity<List<FetchUserTrainingsResponseDTO>> getTrainerTraining(@RequestParam("username") String username, FetchTrainerTrainingsRequestDTO request) {
        return ResponseEntity.ok(trainingService.findTrainerTrainings(username, request));
    }
}
