package com.epam.spring.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true, exclude = {"trainings"})
@Entity
@Table(name = "trainees", schema = "gym")
public class Trainee extends BaseEntity {

    @Column(name = "date_of_birth")
    private LocalDate dataOfBirth;

    @Column(name = "address")
    private String address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "trainee", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Training> trainings;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trainee trainee = (Trainee) o;
        return Objects.equals(dataOfBirth, trainee.dataOfBirth) && Objects.equals(address, trainee.address) && Objects.equals(user, trainee.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dataOfBirth, address);
    }
}
