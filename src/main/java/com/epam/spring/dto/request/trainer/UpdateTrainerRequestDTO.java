package com.epam.spring.dto.request.trainer;

import com.epam.spring.dto.TrainingTypeDTO;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Firstname is required")
    private String firstName;
    @NotBlank(message = "Lastname is required")
    private String lastName;
    private TrainingTypeDTO trainingType;
    @NotBlank(message = "Lastname is required")
    private Boolean active;
}
