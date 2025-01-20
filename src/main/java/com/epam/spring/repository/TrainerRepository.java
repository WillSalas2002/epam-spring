package com.epam.spring.repository;

import com.epam.spring.model.Trainer;
import com.epam.spring.model.TrainingType;
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
public class TrainerRepository implements TrainerSpecificOperationsRepository {

    public static final String FIND_ALL_QUERY = "SELECT t FROM Trainer t";
    public static final String FIND_BY_ID_QUERY = "SELECT t FROM Trainer t LEFT JOIN FETCH t.trainings LEFT JOIN FETCH t.specialization WHERE t.id = :id";
    public static final String FIND_BY_USERNAME_QUERY = "SELECT t FROM Trainer t LEFT JOIN FETCH t.trainings LEFT JOIN FETCH t.specialization WHERE t.user.username =: username";
    public static final String FIND_BY_TRAINEE_USERNAME_QUERY = """
            SELECT DISTINCT t
            FROM Trainer t
                     JOIN User u ON t.user.id = u.id
            WHERE t.id NOT IN (SELECT tr.id
                               FROM Training trn
                                        JOIN Trainer tr ON tr.id = trn.trainer.id
                                        JOIN Trainee tn ON tn.id = trn.trainee.id
                                        JOIN User tu ON tu.id = tn.user.id
                               WHERE tu.username =: username)
            """;

    private final SessionFactory sessionFactory;

    @Override
    public Trainer create(Trainer trainer) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            TrainingType mergedSpecialization = session.get(TrainingType.class, trainer.getSpecialization().getId());
            trainer.setSpecialization(mergedSpecialization);
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

            session.createQuery(FIND_BY_ID_QUERY, Trainer.class)
                    .setParameter("id", updatedTrainer.getId())
                    .getSingleResult();
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
    // найти список тренеров кто не является тренером текущего ученика
    public List<Trainer> findUnassignedTrainersByTraineeUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(FIND_BY_TRAINEE_USERNAME_QUERY, Trainer.class)
                    .setParameter("username", username)
                    .getResultList();
        }
    }
}
