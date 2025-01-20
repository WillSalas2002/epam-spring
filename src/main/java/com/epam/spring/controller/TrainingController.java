package com.epam.spring.controller;

import com.epam.spring.dto.request.training.CreateTrainingRequestDTO;
import com.epam.spring.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/trainings", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class TrainingController {

    private final TrainingService trainingService;

    public void create(CreateTrainingRequestDTO request) {
        trainingService.create(request);
    }

}
