package com.epam.spring.repository;

import com.epam.spring.model.Trainer;
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
public class TrainerRepository implements BaseOperationsRepository<Trainer>, ExtendedOperationsRepository<Trainer>, TrainerSpecificOperationsRepository {

    public static final String FIND_ALL_QUERY = "SELECT t FROM Trainer t";
    public static final String FIND_BY_ID_QUERY = "SELECT t FROM Trainer t WHERE id =: id";
    public static final String FIND_BY_USERNAME_QUERY = "SELECT t FROM Trainer t WHERE t.user.username =: username";
//    public static final String FIND_BY_TRAINEE_USERNAME_QUERY = "SELECT t FROM Trainer t WHERE t NOT IN (SELECT t FROM Trainee tee JOIN tee..trainers t WHERE tee.user.username =: username)";

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
            List<Trainer> trainers = session.createQuery(FIND_ALL_QUERY, Trainer.class)
                    .getResultList();
            transaction.commit();
            return trainers;
        }
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Trainer trainer = session.createQuery(FIND_BY_ID_QUERY, Trainer.class)
                    .setParameter("id", id)
                    .uniqueResult();
            return Optional.ofNullable(trainer);
        }
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Trainer trainer = session.createQuery(FIND_BY_USERNAME_QUERY, Trainer.class)
                    .setParameter("username", username)
                    .uniqueResult();

            return Optional.ofNullable(trainer);
        }
    }


    @Override
    public Trainer update(Trainer updatedTrainer) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(updatedTrainer.getUser());
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
//        try (Session session = sessionFactory.openSession()) {
//            return session.createQuery(FIND_BY_TRAINEE_USERNAME_QUERY, Trainer.class)
//                    .setParameter("username", username)
//                    .getResultList();
//        }
        return null;
    }
}
