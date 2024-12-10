package com.epam.spring;

import com.epam.spring.config.AppConfig;
import com.epam.spring.facade.GymCrmFacade;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    private static final Log LOGGER = LogFactory.getLog(Main.class);

    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        GymCrmFacade facade = context.getBean(GymCrmFacade.class);

        List<Trainee> trainees = facade.findAllTrainees();
        LOGGER.info("All Trainees after initialization: " + trainees);

        List<Trainer> trainers = facade.findAllTrainers();
        LOGGER.info("All Trainers after initialization: " + trainers);

        List<Training> trainings = facade.findAllTrainings();
        LOGGER.info("All Trainings after initialization: " + trainings);

        createEntitiesExample(facade);


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
                "Strong cardio",
                true
        ));

        Training training = facade.createTraining(new Training(
                trainee,
                trainer,
                "Iron man Training",
                TrainingType.STRENGTH_TRAINING,
                LocalDateTime.now().plusDays(2),
                120
        ));
    }
}
