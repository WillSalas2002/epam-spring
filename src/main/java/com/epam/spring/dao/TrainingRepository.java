package com.epam.spring.dao;

import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

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
            TrainingType mergedTrainingType = session.merge(training.getTrainingType());
            training.setTrainingType(mergedTrainingType);
            session.persist(training);
            transaction.commit();
        }
        return training;
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
            return session.createQuery("SELECT t FROM Training t WHERE t.id =: id", Training.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }
}
