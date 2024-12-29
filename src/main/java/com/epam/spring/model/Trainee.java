package com.epam.spring.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuperBuilder

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "_trainee", schema = "gym")
public class Trainee extends User {

    @Column(name = "date_of_birth")
    private LocalDate dataOfBirth;

    @Column(name = "address")
    private String address;

    @OneToMany(mappedBy = "trainee", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Training> trainings;

    @ManyToMany(mappedBy = "trainees", fetch = FetchType.LAZY)
    private List<Trainer> trainers;

    public Trainee(String firstName, String lastName, LocalDate dataOfBirth, String address, boolean isActive) {
        super(firstName, lastName, isActive);
        this.dataOfBirth = dataOfBirth;
        this.address = address;
    }

    public List<Trainer> getTrainers() {
        if (trainers == null) {
            return new ArrayList<>();
        }
        return trainers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Trainee trainee = (Trainee) o;
        return Objects.equals(dataOfBirth, trainee.dataOfBirth) && Objects.equals(address, trainee.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dataOfBirth, address);
    }
}
