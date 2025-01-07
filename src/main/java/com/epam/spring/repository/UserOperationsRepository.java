package com.epam.spring.repository;

public interface UserOperationsRepository {

    boolean existsByUsername(String username);
}
