package com.epam.spring.dto.response.training;

import com.epam.spring.dto.TrainingTypeDTO;
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
public class FetchUserTrainingsResponseDTO {

    private String trainingName;
    private LocalDateTime trainingDate;
    private TrainingTypeDTO trainingType;
    private Integer duration;
    private String username;
}
