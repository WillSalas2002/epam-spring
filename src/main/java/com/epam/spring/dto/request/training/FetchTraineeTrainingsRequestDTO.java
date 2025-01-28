package com.epam.spring.dto.request.training;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FetchTraineeTrainingsRequestDTO {

    @NotBlank(message = "Trainee Username is required")
    private String traineeUsername;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String trainerUsername;
    private String trainingTypeName;

    public FetchTraineeTrainingsRequestDTO(String traineeUsername) {
        this.traineeUsername = traineeUsername;
    }
}
