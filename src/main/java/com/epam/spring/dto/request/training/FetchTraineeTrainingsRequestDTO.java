package com.epam.spring.dto.request.training;

import com.epam.spring.dto.TrainingTypeDTO;
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

    private String fromDate;
    private String toDate;
    private String trainerUsername;
    private TrainingTypeDTO trainingType;
}
