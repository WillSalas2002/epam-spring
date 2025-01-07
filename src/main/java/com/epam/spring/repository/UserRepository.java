package com.epam.spring.repository;

import com.epam.spring.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UserRepository implements UserOperationsRepository {

    private final SessionFactory sessionFactory;

    @Override
    public boolean existsByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.createQuery("SELECT u FROM User u WHERE u.username =: username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
            return user != null;
        }
    }
}
