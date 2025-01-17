package com.epam.spring.dto.request.trainer;

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
public class CreateTrainerRequestDTO {

    private String firstName;
    private String lastName;
    private Long trainingTypeId;
}
