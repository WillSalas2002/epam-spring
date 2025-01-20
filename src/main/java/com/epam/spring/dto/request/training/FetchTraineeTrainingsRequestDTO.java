package com.epam.spring.dto.request.training;

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

    private LocalDate fromDate;
    private LocalDate toDate;
    private String trainerUsername;
    private String trainingTypeName;
}
