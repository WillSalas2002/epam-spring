package com.epam.spring.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Objects;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true, exclude = {"trainee", "trainer", "trainingType"})
@Entity
@Table(name = "trainings")
public class Training extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "trainee_id", nullable = false)
    private Trainee trainee;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType trainingType;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    public Training(Trainee trainee, Trainer trainer, String name, TrainingType trainingType, LocalDateTime date, Integer duration) {
        super();
        this.trainee = trainee;
        this.trainer = trainer;
        this.name = name;
        this.trainingType = trainingType;
        this.date = date;
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Training training = (Training) o;
        return Objects.equals(trainee, training.trainee) && Objects.equals(trainer, training.trainer) && Objects.equals(name, training.name) && Objects.equals(trainingType, training.trainingType) && Objects.equals(date, training.date) && Objects.equals(duration, training.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainee, trainer, name, trainingType, date, duration);
    }
}
