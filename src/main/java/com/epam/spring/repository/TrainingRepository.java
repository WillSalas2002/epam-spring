package com.epam.spring.repository;

import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import com.epam.spring.util.QueryBuilder;
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
public class TrainingRepository implements BaseOperationsRepository<Training>, TrainingSpecificOperationsRepository {

    public static final String FIND_TRAINING_BY_ID_QUERY = "SELECT t FROM Training t JOIN FETCH t.trainee JOIN FETCH t.trainer JOIN FETCH t.trainingType WHERE t.id =: id";
    public static final String FIND_ALL_TRAININGS_QUERY = "SELECT t FROM Training t JOIN FETCH t.trainee JOIN FETCH t.trainer JOIN FETCH t.trainingType";

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
            return session.createQuery(FIND_ALL_TRAININGS_QUERY, Training.class)
                    .getResultList();
        }
    }

    @Override
    public Training findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(FIND_TRAINING_BY_ID_QUERY, Training.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }

    @Override
    public List<Training> findTraineeTrainings(String traineeUsername,
                                               LocalDate fromDate,
                                               LocalDate toDate,
                                               String trainerName,
                                               String trainingType) {

        try (Session session = sessionFactory.openSession()) {
            String hqlQuery = QueryBuilder.buildFindTraineeTrainingsQuery(fromDate, toDate, trainerName, trainingType);

            Query<Training> query = session.createQuery(hqlQuery, Training.class);

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

    @Override
    public List<Training> findTrainerTrainings(String trainerUsername,
                                               LocalDate fromDate,
                                               LocalDate toDate,
                                               String traineeName,
                                               String trainingType) {

        try (Session session = sessionFactory.openSession()) {
            String hqlQuery = QueryBuilder.buildFindTrainerTrainings(fromDate, toDate, traineeName, trainingType);

            Query<Training> query = session.createQuery(hqlQuery, Training.class);

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

            return query.getResultList();
        }
    }
}
