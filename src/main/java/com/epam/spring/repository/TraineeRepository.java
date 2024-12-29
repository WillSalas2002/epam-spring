package com.epam.spring.repository;

import com.epam.spring.model.Trainee;
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
public class TraineeRepository implements BaseOperationsDAO<Trainee>, ExtendedOperationsDAO<Trainee> {

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
            return session.createQuery("SELECT t FROM Trainee t", Trainee.class)
                    .getResultList();
        }
    }

    @Override
    public Trainee findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            List<Trainee> trainees = session.createQuery("FROM Trainee WHERE id =: id", Trainee.class)
                    .setParameter("id", id)
                    .getResultList();
            return trainees.isEmpty() ? null : trainees.get(0);
        }
    }

    @Override
    public Trainee findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            List<Trainee> trainees = session.createQuery("""
                            SELECT t FROM Trainee t
                            WHERE t.username =: username
                            """, Trainee.class)
                    .setParameter("username", username)
                    .getResultList();

            return trainees.isEmpty() ? null : trainees.get(0);
        }
    }

    @Override
    public Trainee update(Trainee updatedTrainee) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
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

    public void deleteByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createQuery("DELETE FROM Trainee t WHERE t.username =: username")
                    .setParameter("username", username)
                    .executeUpdate();
            transaction.commit();
        }
    }
}
