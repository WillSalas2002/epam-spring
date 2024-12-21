package com.epam.spring.model;

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
public class Training extends BaseEntity {

    private Trainee trainee;
    private Trainer trainer;
    private String name;
    private TrainingType type;
    private LocalDateTime date;
    private Integer duration;

    public Training(Trainee trainee, Trainer trainer, String name, TrainingType type, LocalDateTime date, Integer duration) {
        super();
        this.trainee = trainee;
        this.trainer = trainer;
        this.name = name;
        this.type = type;
        this.date = date;
        this.duration = duration;
    }
}
