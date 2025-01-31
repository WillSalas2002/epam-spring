//package com.epam.spring.repository.impl;
//
//import com.epam.spring.model.User;
//import com.epam.spring.repository.base.UserOperationsRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.Transaction;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Slf4j
//@RequiredArgsConstructor
//@Repository
//public class UserRepository implements UserOperationsRepository {
//
//    private final SessionFactory sessionFactory;
//
//    @Override
//    public Optional<User> findByUsername(String username) {
//        try (Session session = sessionFactory.openSession()) {
//            User user = session.createQuery("SELECT u FROM User u WHERE u.username =: username", User.class)
//                    .setParameter("username", username)
//                    .uniqueResult();
//            return Optional.ofNullable(user);
//        }
//    }
//
//    @Override
//    public void update(User user) {
//        try (Session session = sessionFactory.openSession()) {
//            Transaction transaction = session.beginTransaction();
//            session.merge(user);
//            transaction.commit();
//        }
//    }
//}
