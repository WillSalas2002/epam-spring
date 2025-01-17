package com.epam.spring.dto.response.trainer;

import com.epam.spring.dto.TrainingTypeDTO;
import com.epam.spring.dto.response.trainee.TraineeResponseDTO;
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
public class UpdateTrainerResponseDTO {

    private String username;
    private String firstName;
    private String lastName;
    private TrainingTypeDTO specialization;
    private Boolean active;
    private List<TraineeResponseDTO> trainees;
}
