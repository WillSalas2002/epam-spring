package com.epam.spring.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Objects;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true, exclude = {"trainee", "trainer", "trainingType"})
@Entity
@Table(name = "trainings", uniqueConstraints = {@UniqueConstraint(columnNames = {"trainee_id", "date"})})
public class Training extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id", nullable = false)
    private Trainee trainee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType trainingType;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "duration", nullable = false)
    private Integer duration;

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
