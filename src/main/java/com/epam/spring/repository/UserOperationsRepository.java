package com.epam.spring.repository;

import com.epam.spring.model.User;

import java.util.Optional;

public interface UserOperationsRepository {

    Optional<User> findByUsername(String username);
    void update(User user);
}
