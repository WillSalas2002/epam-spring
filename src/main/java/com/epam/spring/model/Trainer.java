package com.epam.spring.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@ToString
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

    public Trainer(String firstName, String lastName, boolean isActive) {
        super(firstName, lastName, isActive);
    }

    public Trainer(String firstName, String lastName, TrainingType specialization, boolean isActive) {
        super(firstName, lastName, isActive);
        this.specialization = specialization;
    }
}
