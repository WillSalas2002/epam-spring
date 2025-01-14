package com.epam.spring;

import com.epam.spring.config.AppConfig;
import com.epam.spring.facade.GymCrmFacade;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import com.epam.spring.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
public class Main {
    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        GymCrmFacade facade = context.getBean(GymCrmFacade.class);

        Trainee trainee = facade.createTrainee(buildTrainee("Will", "Salas"));
        TrainingType trainingType = buildTrainingType();
        Trainer trainer = facade.createTrainer(buildTrainer("Adam", "Wilson", trainingType));
        facade.createTraining(buildTraining(trainee, trainer, trainingType), trainee.getUser().getUsername(), trainee.getUser().getPassword());
    }

    private static Trainee buildTrainee(String firstName, String lastName) {
        return Trainee.builder()
                .user(User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .isActive(true)
                        .build()
                )
                .dataOfBirth(LocalDate.now().minusYears(25))
                .address("Test Address")
                .build();
    }

    private static Trainer buildTrainer(String firstName, String lastName, TrainingType specialization) {
        return Trainer.builder()
                .user(User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .isActive(true)
                        .build()
                )
                .specialization(specialization)
                .build();
    }

    private static Training buildTraining(Trainee trainee, Trainer trainer, TrainingType trainingType) {
        return Training.builder()
                .name("Best Cardio")
                .duration(90)
                .date(LocalDateTime.now().plusDays(2))
                .trainee(trainee)
                .trainer(trainer)
                .trainingType(trainingType)
                .build();
    }

    private static TrainingType buildTrainingType() {
        return TrainingType.builder()
                .trainingTypeName("Cardio")
                .build();
    }
}
