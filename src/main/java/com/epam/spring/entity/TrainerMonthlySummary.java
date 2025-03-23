package com.epam.spring.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Month;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerMonthlySummary {

    private String username;
    private String firstName;
    private String lastName;
    private boolean status;
    private Map<Integer, Map<Month, Integer>> summary;
}
