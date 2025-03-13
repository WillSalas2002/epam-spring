package com.epam.spring.entity;

import lombok.Builder;
import lombok.Data;

import java.time.Month;
import java.util.Map;

@Builder
@Data
public class TrainerMonthlySummary {

    private String username;
    private String firstName;
    private String lastName;
    private boolean status;
    private Map<Integer, Map<Month, Integer>> summary;
}
