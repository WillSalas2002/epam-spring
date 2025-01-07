package com.epam.spring.repository;

import com.epam.spring.model.Trainer;
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
public class TrainerRepository implements BaseOperationsRepository<Trainer>, ExtendedOperationsRepository<Trainer>, TrainerSpecificOperationsRepository {

    private final SessionFactory sessionFactory;

    @Override
    public Trainer create(Trainer trainer) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(trainer);
            transaction.commit();
        } catch (Exception e) {
            log.warn(e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
        }
        return trainer;
    }

    @Override
    public List<Trainer> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<Trainer> trainers = session.createQuery("SELECT t FROM Trainer t LEFT JOIN FETCH t.specialization", Trainer.class)
                    .getResultList();
            transaction.commit();
            return trainers;
        }
    }

    @Override
    public Trainer findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            List<Trainer> trainers = session.createQuery("SELECT t FROM Trainer t WHERE id =: id", Trainer.class)
                    .setParameter("id", id)
                    .getResultList();
            return trainers.isEmpty() ? null : trainers.get(0);
        }
    }

    @Override
    public Trainer findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            List<Trainer> trainers = session.createQuery("""
                            SELECT t FROM Trainer t
                                LEFT JOIN FETCH t.trainees
                            WHERE t.username =: username
                            """, Trainer.class)
                    .setParameter("username", username)
                    .getResultList();

            return trainers.isEmpty() ? null : trainers.get(0);
        }
    }


    @Override
    public Trainer update(Trainer updatedTrainer) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Trainer mergedTrainer = session.merge(updatedTrainer);
            transaction.commit();
            return mergedTrainer;
        }
    }

    @Override
    public void delete(Trainer trainer) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(trainer);
            transaction.commit();
        }
    }

    @Override
    public List<Trainer> findTrainersByTraineeUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("""
                            SELECT t FROM Trainer t
                            WHERE t NOT IN (
                                SELECT t
                                FROM Trainee tee
                                    JOIN tee.trainers t
                                WHERE tee.username =: username
                            )""", Trainer.class)
                    .setParameter("username", username)
                    .getResultList();
        }
    }
}
