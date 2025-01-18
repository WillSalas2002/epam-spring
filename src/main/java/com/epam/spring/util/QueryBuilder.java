package com.epam.spring.util;

import com.epam.spring.model.Training;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalDateTime;

public class QueryBuilder {

    public static final String FIND_TRAINER_TRAININGS_LIST_BASE_QUERY = """
            SELECT t FROM Training t
                JOIN FETCH t.trainer trainer
                JOIN FETCH t.trainee trainee
                JOIN FETCH t.trainingType trainingType
            WHERE trainer.user.username = :trainerUsername
            """;
    public static final String FIND_TRAINEE_TRAININGS_LIST_BASE_QUERY = """
            SELECT t FROM Training t
                JOIN FETCH t.trainer trainer
                JOIN FETCH t.trainee trainee
                JOIN FETCH t.trainingType trainingType
            WHERE trainee.user.username = :traineeUsername
            """;

    public static Query<Training> buildFindTraineeTrainingsQuery(Session session,
                                                                 String traineeUsername,
                                                                 LocalDateTime fromDate,
                                                                 LocalDateTime toDate,
                                                                 String trainerName,
                                                                 String trainingTypeName) {
        StringBuilder baseHQL = new StringBuilder(FIND_TRAINEE_TRAININGS_LIST_BASE_QUERY);
        StringBuilder hql = buildHQL(baseHQL, fromDate, toDate, trainerName, trainingTypeName);

        Query<Training> query = session.createQuery(hql.toString(), Training.class);

        query.setParameter("traineeUsername", traineeUsername);
        if (fromDate != null) {
            query.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            query.setParameter("toDate", toDate);
        }
        if (trainerName != null) {
            query.setParameter("trainerName", "%" + trainerName + "%");
        }
        if (trainingTypeName != null) {
            query.setParameter("trainingTypeName", "%" + trainingTypeName + "%");
        }
        return query;
    }

    public static Query<Training> buildFindTrainerTrainings(Session session,
                                                            String trainerUsername,
                                                            LocalDateTime fromDate,
                                                            LocalDateTime toDate,
                                                            String traineeName) {
        StringBuilder baseHQL = new StringBuilder(FIND_TRAINER_TRAININGS_LIST_BASE_QUERY);
        StringBuilder hql = buildHQL(baseHQL, fromDate, toDate, traineeName, null);

        Query<Training> query = session.createQuery(hql.toString(), Training.class);

        query.setParameter("trainerUsername", trainerUsername);
        if (fromDate != null) {
            query.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            query.setParameter("toDate", toDate);
        }
        if (traineeName != null) {
            query.setParameter("traineeName", "%" + traineeName + "%");
        }
        return query;
    }

    private static StringBuilder buildHQL(StringBuilder hql,
                                          LocalDateTime fromDate,
                                          LocalDateTime toDate,
                                          String secondaryUsername,
                                          String trainingTypeName) {
        if (fromDate != null) {
            hql.append("AND t.date >= :fromDate ");
        }
        if (toDate != null) {
            hql.append("AND t.date <= :toDate ");
        }
        if (secondaryUsername != null) {
            hql.append("AND trainer.user.username LIKE :trainerName ");
        }
        if (trainingTypeName != null) {
            hql.append("AND trainingType.trainingTypeName LIKE :trainingTypeName ");
        }
        return hql;
    }
}
