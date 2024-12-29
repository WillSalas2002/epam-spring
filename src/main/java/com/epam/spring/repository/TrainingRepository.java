package com.epam.spring.repository;

import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class TrainingRepository implements BaseOperationsDAO<Training> {

    private final SessionFactory sessionFactory;

    @Override
    public Training create(Training training) {
        Transaction transaction;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            TrainingType trainingType = session.merge(training.getTrainingType());

            Trainer trainer = session.get(Trainer.class, training.getTrainer().getId());
            Trainee trainee = session.get(Trainee.class, training.getTrainee().getId());

            if (!trainer.getTrainees().contains(trainee)) {
                trainer.addTrainee(trainee);
            }

            training.setTrainingType(trainingType);
            training.setTrainer(trainer);
            training.setTrainee(trainee);

            session.persist(training);

            transaction.commit();
            return training;
        }
    }

    @Override
    public List<Training> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("""
                            SELECT t FROM Training t
                                JOIN FETCH t.trainee
                                JOIN FETCH t.trainer
                                JOIN FETCH t.trainingType
                            """, Training.class)
                    .getResultList();
        }
    }

    @Override
    public Training findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("""
                            SELECT t FROM Training t
                                JOIN FETCH t.trainee
                                JOIN FETCH t.trainer
                                JOIN FETCH t.trainingType
                            WHERE t.id =: id
                            """, Training.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }

    public List<Training> findTraineeTrainings(String traineeUsername,
                                               LocalDate fromDate,
                                               LocalDate toDate,
                                               String trainerName,
                                               String trainingType) {

        try (Session session = sessionFactory.openSession()) {
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
            return query.getResultList();
        }
    }

    public List<Training> findTrainerTrainings(String trainerUsername,
                                               LocalDate fromDate,
                                               LocalDate toDate,
                                               String traineeName,
                                               String trainingType) {

        try (Session session = sessionFactory.openSession()) {
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

            Query<Training> query = session.createQuery(hql.toString(), Training.class);
            query.setParameter("trainerUsername", trainerUsername);

            if (fromDate != null) {
                query.setParameter("fromDate", fromDate);
            }
            if (toDate != null) {
                query.setParameter("toDate", toDate);
            }
            if (traineeName != null && !traineeName.isEmpty()) {
                query.setParameter("trainerName", "%" + traineeName + "%");
            }
            if (trainingType != null && !trainingType.isEmpty()) {
                query.setParameter("trainingType", "%" + trainingType + "%");
            }
            return query.getResultList();
        }
    }
}
