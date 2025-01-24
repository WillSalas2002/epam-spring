package com.epam.spring.repository.impl;

import com.epam.spring.model.TrainingType;
import com.epam.spring.repository.base.TrainingTypeOperationsRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TrainingTypeRepository implements TrainingTypeOperationsRepository {

    private final SessionFactory sessionFactory;

    @Override
    public List<TrainingType> findAll() {
        try(Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM TrainingType", TrainingType.class)
                    .getResultList();
        }
    }
}
