package com.epam.spring.dto.request.training;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FetchTrainerTrainingsRequestDTO {

    @NotBlank(message = "Trainer username is required")
    private String trainerUsername;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String traineeUsername;
}
