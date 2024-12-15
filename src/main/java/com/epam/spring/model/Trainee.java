package com.epam.spring.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@SuperBuilder
@ToString
@Getter
@Setter
@NoArgsConstructor
public class Trainee extends User {

    private LocalDate dataOfBirth;
    private String address;

    public Trainee(String firstName, String lastName, boolean isActive) {
        super(firstName, lastName, isActive);
    }

    public Trainee(String firstName, String lastName, LocalDate dataOfBirth, String address, boolean isActive) {
        super(firstName, lastName, isActive);
        this.dataOfBirth = dataOfBirth;
        this.address = address;
    }
}
