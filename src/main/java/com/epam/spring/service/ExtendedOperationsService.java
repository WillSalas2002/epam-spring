package com.epam.spring.service;

public interface ExtendedOperationsService <K>{

    void delete(K entity);
    K update(K entity);
}
