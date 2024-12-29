package com.epam.spring;

import com.epam.spring.config.AppConfig;
import com.epam.spring.facade.GymCrmFacade;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class Main {

    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        GymCrmFacade facade = context.getBean(GymCrmFacade.class);

        List<Trainee> trainees = facade.findAllTrainees();
//        LOGGER.info("All Trainees after initialization: " + trainees);

        List<Trainer> trainers = facade.findAllTrainers();
//        LOGGER.info("All Trainers after initialization: " + trainers);

        List<Training> trainings = facade.findAllTrainings();
//        LOGGER.info("All Trainings after initialization: " + trainings);

//        Trainee trainee = facade.findTraineeById(1L);
//        trainee.setFirstName("Adam");
//        Trainee trainee1 = facade.updateTrainee(trainee);
//        System.out.println(trainee1.getUsername());
    }

    private static void createEntitiesExample(GymCrmFacade facade) {
        Trainee trainee = facade.createTrainee(new Trainee(
                "Will",
                "Salas",
                LocalDate.now().minusYears(10),
                "Nukus",
                false));

        Trainer trainer = facade.createTrainer(new Trainer(
                "Simon",
                "Anderson",
                TrainingType.builder().trainingTypeName("Strong cardio").build(),
                true
        ));

        facade.createTraining(new Training(
                trainee,
                trainer,
                "Iron man Training",
                TrainingType.builder().id(1L).trainingTypeName("Strong cardio").build(),
                LocalDateTime.now().plusDays(2),
                120
        ));
    }
}
