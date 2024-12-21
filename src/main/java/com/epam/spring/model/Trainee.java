package com.epam.spring.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@SuperBuilder
@ToString
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

    @OneToMany(mappedBy = "trainee", fetch = FetchType.LAZY)
    private List<Training> trainings;

    public Trainee(String firstName, String lastName, boolean isActive) {
        super(firstName, lastName, isActive);
    }

    public Trainee(String firstName, String lastName, LocalDate dataOfBirth, String address, boolean isActive) {
        super(firstName, lastName, isActive);
        this.dataOfBirth = dataOfBirth;
        this.address = address;
    }
}
