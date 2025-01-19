package com.epam.spring.dto.response.trainee;

import com.epam.spring.dto.response.trainer.TrainerResponseDTO;
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
public class UpdateTraineeResponseDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String address;
    private boolean isActive;
    private List<TrainerResponseDTO> trainers;
}
