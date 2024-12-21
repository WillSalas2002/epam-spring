package com.epam.spring.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "_training", schema = "gym")
public class Training extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "trainee_id")
    private Trainee trainee;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "training_type_id")
    private TrainingType trainingType;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "duration")
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
}
