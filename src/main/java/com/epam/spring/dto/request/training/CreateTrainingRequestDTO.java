package com.epam.spring.dto.request.training;

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

    private String traineeUsername;
    private String trainerUsername;
    private String trainingName;
    private String trainingDate;
    private String duration;
}
