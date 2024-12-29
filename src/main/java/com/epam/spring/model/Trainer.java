package com.epam.spring.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuperBuilder

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "_trainer", schema = "gym")
public class Trainer extends User {

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "training_type_id")
    private TrainingType specialization;

    @OneToMany(mappedBy = "trainer", fetch = FetchType.LAZY)
    private List<Training> trainings;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            schema = "gym",
            name = "trainer_trainee",
            joinColumns = @JoinColumn(name = "trainer_id"),
            inverseJoinColumns = @JoinColumn(name = "trainee_id")
    )
    private List<Trainee> trainees;

    public Trainer(String firstName, String lastName, TrainingType specialization, boolean isActive) {
        super(firstName, lastName, isActive);
        this.specialization = specialization;
    }

    public List<Trainee> getTrainees() {
        if (trainees == null) {
            return new ArrayList<>();
        }
        return trainees;
    }

    public void addTrainee(Trainee trainee) {
        getTrainees().add(trainee);
        trainee.getTrainers().add(this);
    }

    public void removeTrainee(Trainee trainee) {
        this.trainees.remove(trainee);
        trainee.getTrainers().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Trainer trainer = (Trainer) o;
        return Objects.equals(specialization, trainer.specialization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), specialization);
    }
}
