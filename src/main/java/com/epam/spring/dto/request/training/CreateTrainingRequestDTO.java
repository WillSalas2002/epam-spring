package com.epam.spring.dto.request.training;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTrainingRequestDTO {

    @NotBlank(message = "Trainee username is required")
    private String traineeUsername;
    @NotBlank(message = "Trainer username is required")
    private String trainerUsername;
    @NotBlank(message = "Training name is required")
    private String trainingName;
    @NotBlank(message = "Training date is required")
    private String trainingDate;
    @NotBlank(message = "Training duration is required")
    private String duration;
}
