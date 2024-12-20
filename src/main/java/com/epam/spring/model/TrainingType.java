package com.epam.spring.model;

public enum TrainingType {

    YOGA("Yoga"),
    CARDIO("Cardio"),
    CROSS_FIT("Cross Fit"),
    STRENGTH_TRAINING("Strengh Training"),
    FUNCTIONAL_TRAINING("Functional Training");

    private final String name;

    TrainingType(String name) {
        this.name = name;
    }
}
