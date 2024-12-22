package com.epam.spring.dao;

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
public class TrainerRepository implements BaseOperationsDAO<Trainer>, ExtendedOperationsDAO<Trainer> {

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
            return session.createQuery("SELECT t FROM Trainer t JOIN FETCH t.specialization", Trainer.class)
                    .getResultList();
        }
    }

    @Override
    public Trainer findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT t FROM Trainer t WHERE id =: id", Trainer.class)
                    .setParameter("id", id)
                    .getSingleResult();
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
            session.remove(trainer);
        }
    }
}
