package com.epam.spring.model;

import java.time.LocalDate;

public class Trainee extends User {

    private LocalDate dataOfBirth;
    private String address;

    public Trainee() {
    }

    public Trainee(String firstName, String lastName, boolean isActive) {
        super(firstName, lastName, isActive);
    }

    public Trainee(String firstName, String lastName, LocalDate dataOfBirth, String address, boolean isActive) {
        super(firstName, lastName, isActive);
        this.dataOfBirth = dataOfBirth;
        this.address = address;
    }

    public LocalDate getDataOfBirth() {
        return dataOfBirth;
    }

    public void setDataOfBirth(LocalDate dataOfBirth) {
        this.dataOfBirth = dataOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", Trainee{" +
                "dataOfBirth=" + dataOfBirth +
                ", address='" + address + '\'' +
                '}';
    }
}
