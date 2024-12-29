package com.epam.spring.util;

import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EntityBuilder {

    public static Trainer buildTrainer(String[] trainerPieces) {
        return Trainer.builder()
                .firstName(trainerPieces[1])
                .lastName(trainerPieces[2])
                .username(trainerPieces[3])
                .password(trainerPieces[4])
//                .specialization(trainerPieces[5])
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

    public static Training buildTraining(String[] trainingPieces, Trainee trainee, Trainer trainer) {
        return Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .name(trainingPieces[3])
//                .type(new TrainingType())
                .date(LocalDateTime.parse(trainingPieces[4]))
                .duration(Integer.parseInt(trainingPieces[5]))
                .build();
    }


}
