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
public class FetchTrainerTrainingsRequestDTO {

    @NotBlank(message = "Trainer username is required")
    private String trainerUsername;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String traineeUsername;

    public FetchTrainerTrainingsRequestDTO(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }
}
