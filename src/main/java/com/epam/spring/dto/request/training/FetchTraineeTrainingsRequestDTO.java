package com.epam.spring.dto.request.training;

import com.epam.spring.dto.TrainingTypeDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FetchTraineeTrainingsRequestDTO {

    @NotBlank(message = "Trainee username is required")
    private String traineeUsername;
    private String fromDate;
    private String toDate;
    private String trainerUsername;
    private TrainingTypeDTO trainingType;
}
