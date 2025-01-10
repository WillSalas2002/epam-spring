package com.epam.spring.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Objects;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "trainers", schema = "gym")
public class Trainer extends BaseEntity {

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType specialization;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "trainer", fetch = FetchType.LAZY)
    private List<Training> trainings;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trainer trainer = (Trainer) o;
        return Objects.equals(specialization, trainer.specialization) && Objects.equals(user, trainer.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), specialization);
    }
}
