package com.epam.spring.dto.response.trainer;

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
public class FetchTrainerResponseDTO {

    private String firstName;
    private String lastName;
    private String specialization;
    private Boolean active;
    private List<TraineeResponseDTO> trainees;
}
