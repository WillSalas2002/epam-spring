package com.epam.spring.model;

public class Trainer extends User {

    private String specialization;

    public Trainer() {
    }

    public Trainer(String firstName, String lastName, boolean isActive) {
        super(firstName, lastName, isActive);
    }

    public Trainer(String firstName, String lastName, String specialization, boolean isActive) {
        super(firstName, lastName, isActive);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", Trainer{" +
                "specialization='" + specialization + '\'' +
                '}';
    }
}
