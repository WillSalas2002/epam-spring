package com.epam.spring.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserActivationRequestDTO {

    @NotBlank(message = "Username is required")
    private String username;
    @NotNull(message = "Is active is required")
    private Boolean active;
}
