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

import java.util.List;

@SuperBuilder
@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "_training_type", schema = "gym")
public class TrainingType extends BaseEntity {

    @Column(name = "training_type_name")
    private String trainingTypeName;

    @OneToMany(mappedBy = "specialization", fetch = FetchType.LAZY)
    private List<Trainer> trainers;

    @OneToMany(mappedBy = "trainingType", fetch = FetchType.LAZY)
    private List<Training> trainings;
}
