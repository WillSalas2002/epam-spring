package com.epam.spring.repository.impl;

import com.epam.spring.error.exception.UniqueConstraintException;
import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import com.epam.spring.repository.base.TrainingSpecificOperationsRepository;
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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class TrainingRepository implements TrainingSpecificOperationsRepository {

    public static final String FIND_TRAINING_BY_ID_QUERY = "SELECT t FROM Training t JOIN FETCH t.trainee JOIN FETCH t.trainer JOIN FETCH t.trainingType WHERE t.id =: id";
    public static final String FIND_ALL_TRAININGS_QUERY = "SELECT t FROM Training t JOIN FETCH t.trainee JOIN FETCH t.trainer JOIN FETCH t.trainingType";

    private final SessionFactory sessionFactory;

    @Override
    public Training create(Training training) {
        Transaction transaction;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            TrainingType trainingType = session.get(TrainingType.class, training.getTrainingType().getId());

            Trainer trainer = session.get(Trainer.class, training.getTrainer().getId());
            Trainee trainee = session.get(Trainee.class, training.getTrainee().getId());

            training.setTrainingType(trainingType);
            training.setTrainer(trainer);
            training.setTrainee(trainee);

            session.persist(training);

            transaction.commit();
        } catch (RuntimeException e) {
            throw new UniqueConstraintException("This Training already exists");
        }
        return training;
    }

    public List<Training> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(FIND_ALL_TRAININGS_QUERY, Training.class)
                    .getResultList();
        }
    }

    public Optional<Training> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Training training = session.createQuery(FIND_TRAINING_BY_ID_QUERY, Training.class)
                    .setParameter("id", id)
                    .getSingleResult();

            return Optional.ofNullable(training);
        }
    }

    @Override
    public List<Training> findTraineeTrainings(String traineeUsername,
                                               LocalDate fromDate,
                                               LocalDate toDate,
                                               String trainerName,
                                               String trainingTypeName) {
        try (Session session = sessionFactory.openSession()) {
            Query<Training> query = QueryBuilder.buildFindTraineeTrainingsQuery(
                    session, traineeUsername, fromDate, toDate, trainerName, trainingTypeName);

            return query.getResultList();
        }
    }

    @Override
    public List<Training> findTrainerTrainings(String trainerUsername,
                                               LocalDate fromDate,
                                               LocalDate toDate,
                                               String traineeName) {
        try (Session session = sessionFactory.openSession()) {
            Query<Training> query = QueryBuilder.buildFindTrainerTrainings(
                    session, trainerUsername, fromDate, toDate, traineeName);

            return query.getResultList();
        }
    }
}
