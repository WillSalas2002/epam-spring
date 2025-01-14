package com.epam.spring.validation;

import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import com.epam.spring.model.User;
import org.springframework.stereotype.Component;

@Component
public class Validator {

    public void validateUser(User user) {
        if (user.getFirstName() == null) {
            throw new IllegalArgumentException("Firstname should not be null");
        }

        if (user.getLastName() == null) {
            throw new IllegalArgumentException("Lastname should not be null");
        }
    }

    public void validateTraining(Training training) {
        if (training.getName() == null) {
            throw new IllegalArgumentException("Training name cannot be null");
        }

        if (training.getDate() == null) {
            throw new IllegalArgumentException("Training date cannot be null");
        }

        if (training.getDuration() == null) {
            throw new IllegalArgumentException("Training duration cannot be null");
        }
    }

    public void validateTrainingType(TrainingType trainingType) {
        if (trainingType.getTrainingTypeName() == null) {
            throw new IllegalArgumentException("TrainingType name cannot be null");
        }
    }
}
