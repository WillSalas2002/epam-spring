package com.epam.spring.dto.request.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
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
public class UpdateTraineeTrainerRequestDTO {

    @Pattern(regexp = "^[0-9]+$", message = "The id should only be a number.")
    private String trainingTypeId;
    @NotEmpty(message = "Trainer usernames list cannot be empty.")
    private List<@NotBlank(message = "Trainer username is required") String> trainerUsernames;
}
