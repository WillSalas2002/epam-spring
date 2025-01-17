package com.epam.spring.dto.request.trainer;

import com.epam.spring.dto.TrainingTypeDTO;
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
public class UpdateTrainerRequestDTO {

    private String username;
    private String firstName;
    private String lastName;
    private TrainingTypeDTO trainingType;
    private Boolean active;
}
