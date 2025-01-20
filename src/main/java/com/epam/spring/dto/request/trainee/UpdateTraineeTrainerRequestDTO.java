package com.epam.spring.dto.request.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty(message = "Trainer usernames list cannot be empty.")
    private List<@NotBlank(message = "Trainer username is required") String> trainerUsernames;
}
