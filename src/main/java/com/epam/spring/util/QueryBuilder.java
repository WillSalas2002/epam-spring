package com.epam.spring.util;

import com.epam.spring.model.Training;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalDate;

public class QueryBuilder {

    public static final String FIND_TRAINER_TRAININGS_LIST_BASE_QUERY = "SELECT t FROM Training t JOIN t.trainer trainer JOIN t.trainee trainee JOIN t.trainingType trainingType WHERE trainer.user.username = :trainerUsername";
    public static final String FIND_TRAINEE_TRAININGS_LIST_BASE_QUERY = "SELECT t FROM Training t JOIN t.trainee trainee JOIN t.trainer trainer JOIN t.trainingType trainingType WHERE trainee.user.username = :traineeUsername ";

    public static Query<Training> buildFindTraineeTrainingsQuery(Session session,
                                                                 String traineeUsername,
                                                                 LocalDate fromDate,
                                                                 LocalDate toDate,
                                                                 String trainerName,
                                                                 String trainingType) {
        StringBuilder hql = new StringBuilder(FIND_TRAINEE_TRAININGS_LIST_BASE_QUERY);

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

        Query<Training> query = session.createQuery(hql.toString(), Training.class);

        query.setParameter("traineeUsername", traineeUsername);
        if (fromDate != null) {
            query.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            query.setParameter("toDate", toDate);
        }
        if (trainerName != null && !trainerName.isEmpty()) {
            query.setParameter("trainerName", "%" + trainerName + "%");
        }
        if (trainingType != null && !trainingType.isEmpty()) {
            query.setParameter("trainingType", "%" + trainingType + "%");
        }

        return query;
    }

    public static Query<Training> buildFindTrainerTrainings(Session session,
                                                            String trainerUsername,
                                                            LocalDate fromDate,
                                                            LocalDate toDate,
                                                            String traineeName,
                                                            String trainingType) {
        StringBuilder hql = new StringBuilder(FIND_TRAINER_TRAININGS_LIST_BASE_QUERY);

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

        Query<Training> query = session.createQuery(hql.toString(), Training.class);

        query.setParameter("trainerUsername", trainerUsername);
        if (fromDate != null) {
            query.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            query.setParameter("toDate", toDate);
        }
        if (traineeName != null && !traineeName.isEmpty()) {
            query.setParameter("traineeName", "%" + traineeName + "%");
        }
        if (trainingType != null && !trainingType.isEmpty()) {
            query.setParameter("trainingType", "%" + trainingType + "%");
        }

        return query;
    }
}
