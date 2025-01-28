package com.epam.spring.dto.request.trainee;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTraineeTrainerRequestDTO {

    @NotBlank(message = "Trainee username is required")
    private String traineeUsername;
    @JsonProperty("trainingIdTrainerUsernamePairs")
    private List<TrainingIdTrainerUsernamePair> trainingIdTrainerUsernamePairs;
}
