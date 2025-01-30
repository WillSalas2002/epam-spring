package com.epam.spring.dto.response.trainee;

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
public class TraineeResponseDTO {

    private String username;
    private String firstName;
    private String lastName;
}
