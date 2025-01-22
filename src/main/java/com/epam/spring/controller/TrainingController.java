package com.epam.spring.controller;

import com.epam.spring.dto.request.training.CreateTrainingRequestDTO;
import com.epam.spring.service.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/trainings", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class TrainingController {

    private final TrainingService trainingService;

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody CreateTrainingRequestDTO request) {
        trainingService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
