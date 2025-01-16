package com.epam.spring.dto.request.trainee;

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
public class CreateTraineeRequestDTO {

    @NotBlank(message = "Firstname is required")
    private String firstName;
    @NotBlank(message = "Firstname is required")
    private String lastName;
    private String dateOfBirth;
    private String address;
}
