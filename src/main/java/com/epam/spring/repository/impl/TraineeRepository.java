package com.epam.spring.repository.impl;

import com.epam.spring.model.Trainee;
import com.epam.spring.repository.base.TraineeSpecificOperationsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class TraineeRepository implements TraineeSpecificOperationsRepository {

    public static final String FIND_ALL_QUERY = "SELECT t FROM Trainee t";
    public static final String FIND_BY_ID_QUERY = "FROM Trainee WHERE id =: id";
    public static final String FIND_BY_USERNAME_QUERY = "SELECT t FROM Trainee t LEFT JOIN FETCH t.trainings WHERE t.user.username =: username";

    private final SessionFactory sessionFactory;

    @Override
    public Trainee create(Trainee trainee) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(trainee);
            transaction.commit();
        } catch (Exception e) {
            log.warn(e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
        }
        return trainee;
    }

    @Override
    public List<Trainee> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(FIND_ALL_QUERY, Trainee.class)
                    .getResultList();
        }
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Trainee trainee = session.createQuery(FIND_BY_ID_QUERY, Trainee.class)
                    .setParameter("id", id)
                    .uniqueResult();

            return Optional.ofNullable(trainee);
        }
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Trainee trainee = session.createQuery(FIND_BY_USERNAME_QUERY, Trainee.class)
                    .setParameter("username", username)
                    .uniqueResult();

            return Optional.ofNullable(trainee);
        }
    }

    @Override
    public Trainee update(Trainee updatedTrainee) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(updatedTrainee.getUser());
            Trainee mergedTrainee = session.merge(updatedTrainee);
            transaction.commit();
            return mergedTrainee;
        }
    }

    @Override
    public void delete(Trainee trainee) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(trainee);
            transaction.commit();
        }
    }

    @Override
    public void deleteByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Trainee trainee = session.createQuery(FIND_BY_USERNAME_QUERY, Trainee.class)
                    .setParameter("username", username)
                    .uniqueResult();
            session.remove(trainee);
            transaction.commit();
        }
    }
}
