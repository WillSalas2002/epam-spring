package com.epam.spring.dto.request.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^[0-9]+$", message = "The id should only be a number.")
    private String specializationId;
    @NotNull(message = "Is active is required")
    private Boolean active;
}
