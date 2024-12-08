package com.epam.spring.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Training {

    private UUID uuid;
    private Trainee trainee;
    private Trainer trainer;
    private String name;
    private TrainingType type;
    private LocalDateTime date;
    private Integer duration;

    public Training(Trainee trainee, Trainer trainer, String name, TrainingType type, LocalDateTime date, Integer duration) {
        this.trainee = trainee;
        this.trainer = trainer;
        this.name = name;
        this.type = type;
        this.date = date;
        this.duration = duration;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Trainee getTrainee() {
        return trainee;
    }

    public void setTrainee(Trainee trainee) {
        this.trainee = trainee;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TrainingType getType() {
        return type;
    }

    public void setType(TrainingType type) {
        this.type = type;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Training{" +
                "trainee=" + trainee +
                ", trainer=" + trainer +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", date=" + date +
                ", duration=" + duration +
                '}';
    }
}
