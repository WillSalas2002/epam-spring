package com.epam.spring.repository;

public interface ExtendedOperationsDAO<ENTITY_TYPE> {

    void delete(ENTITY_TYPE entity);
    ENTITY_TYPE update(ENTITY_TYPE entity);
    ENTITY_TYPE findByUsername(String username);
}
