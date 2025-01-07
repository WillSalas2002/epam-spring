package com.epam.spring.util;

import java.time.LocalDate;

public class QueryBuilder {

    public static String buildFindTraineeTrainingsQuery(LocalDate fromDate,
                                                        LocalDate toDate,
                                                        String trainerName,
                                                        String trainingType) {
        StringBuilder hql = new StringBuilder(
                "SELECT t FROM Training t " +
                        "JOIN t.trainee trainee " +
                        "JOIN t.trainer trainer " +
                        "JOIN t.trainingType trainingType " +
                        "WHERE trainee.username = :traineeUsername "
        );

        if (fromDate != null) {
            hql.append("AND t.date >= :fromDate ");
        }
        if (toDate != null) {
            hql.append("AND t.date <= :toDate ");
        }
        if (trainerName != null && !trainerName.isEmpty()) {
            hql.append("AND trainer.firstName LIKE :trainerName ");
        }
        if (trainingType != null && !trainingType.isEmpty()) {
            hql.append("AND trainingType.trainingTypeName LIKE :trainingType ");
        }

        return hql.toString();
    }

    public static String buildFindTrainerTrainings(LocalDate fromDate,
                                                   LocalDate toDate,
                                                   String traineeName,
                                                   String trainingType) {
        StringBuilder hql = new StringBuilder("""
                SELECT t FROM Training t
                    JOIN t.trainer trainer
                    JOIN t.trainee trainee
                    JOIN t.trainingType trainingType
                WHERE trainer.username = :trainerUsername
                """);

        if (fromDate != null) {
            hql.append("AND t.date >= :fromDate ");
        }
        if (toDate != null) {
            hql.append("AND t.date <= :toDate ");
        }
        if (traineeName != null && !traineeName.isEmpty()) {
            hql.append("AND trainee.firstName LIKE :traineeName ");
        }
        if (trainingType != null && !trainingType.isEmpty()) {
            hql.append("AND trainingType.trainingTypeName LIKE :trainingType ");
        }

        return hql.toString();
    }
}
