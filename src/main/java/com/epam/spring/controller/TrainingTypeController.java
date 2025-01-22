package com.epam.spring.controller;

import com.epam.spring.dto.response.TrainingTypeDTO;
import com.epam.spring.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/trainingTypes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    @GetMapping
    public ResponseEntity<List<TrainingTypeDTO>> findAll() {
        return ResponseEntity.ok(trainingTypeService.findAll());
    }
}
