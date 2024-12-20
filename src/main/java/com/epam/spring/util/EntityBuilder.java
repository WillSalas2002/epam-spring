package com.epam.spring.util;

import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import com.epam.spring.model.User;
import com.epam.spring.service.TraineeService;
import com.epam.spring.service.TrainerService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.NoSuchElementException;

public class EntityBuilder {

    public static Trainer buildTrainer(String[] trainerPieces) {
        return Trainer.builder()
                .firstName(trainerPieces[1])
                .lastName(trainerPieces[2])
                .username(trainerPieces[3])
                .password(trainerPieces[4])
                .specialization(trainerPieces[5])
                .isActive(trainerPieces[6].equals(Boolean.TRUE.toString()))
                .build();
    }

    public static Trainee buildTrainee(String[] pieces) {
        return Trainee.builder()
                .firstName(pieces[1])
                .lastName(pieces[2])
                .username(pieces[3])
                .password(pieces[4])
                .dataOfBirth(LocalDate.parse(pieces[5]))
                .address(pieces[6])
                .isActive(pieces[7].equals(Boolean.TRUE.toString()))
                .build();
    }

    public static Training buildTraining(String[] trainingPieces, TraineeService traineeService, TrainerService trainerService) {
        return Training.builder()
                .trainee(getUserByUsername(traineeService.findAll(), trainingPieces[1], "Trainee not found"))
                .trainer(getUserByUsername(trainerService.findAll(), trainingPieces[2], "Trainer not found"))
                .name(trainingPieces[3])
                .type(TrainingType.CARDIO)
                .date(LocalDateTime.parse(trainingPieces[4]))
                .duration(Integer.parseInt(trainingPieces[5]))
                .build();
    }

    private static <T extends User> T getUserByUsername(Collection<T> users, String username, String errorMessage) {
        return users.stream()
                .filter(trainee -> trainee.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(errorMessage));
    }
}
