package com.epam.spring.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
@Getter
@Setter
@NoArgsConstructor
public class Trainer extends User {

    private String specialization;

    public Trainer(String firstName, String lastName, boolean isActive) {
        super(firstName, lastName, isActive);
    }

    public Trainer(String firstName, String lastName, String specialization, boolean isActive) {
        super(firstName, lastName, isActive);
        this.specialization = specialization;
    }
}
