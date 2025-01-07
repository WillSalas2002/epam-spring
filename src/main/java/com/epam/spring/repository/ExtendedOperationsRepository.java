package com.epam.spring.repository;

import java.util.Optional;

public interface ExtendedOperationsRepository<ENTITY_TYPE> {

    void delete(ENTITY_TYPE entity);
    ENTITY_TYPE update(ENTITY_TYPE entity);
    Optional<ENTITY_TYPE> findByUsername(String username);
}
