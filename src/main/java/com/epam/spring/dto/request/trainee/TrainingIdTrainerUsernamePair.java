package com.epam.spring.dto.request.trainee;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainingIdTrainerUsernamePair {

    @JsonProperty("trainingId")
    private Long trainingId;
    @JsonProperty("trainerUsername")
    private String trainerUsername;
}
