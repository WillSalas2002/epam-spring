package com.epam.spring.model;

import java.util.UUID;

public class Trainer extends User {

    private final UUID userId = UUID.randomUUID();
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

    public UUID getUserId() {
        return userId;
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
                "Trainer{" +
                "userId=" + userId +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}
