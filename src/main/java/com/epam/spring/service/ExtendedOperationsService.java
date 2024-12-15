package com.epam.spring.service;

public interface ExtendedOperationsService<ENTITY_TYPE>{

    void delete(ENTITY_TYPE entity);
    ENTITY_TYPE update(ENTITY_TYPE entity);
}
