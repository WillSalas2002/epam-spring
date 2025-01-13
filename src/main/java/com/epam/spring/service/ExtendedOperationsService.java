package com.epam.spring.service;

public interface ExtendedOperationsService<ENTITY_TYPE> extends BaseOperationsService<ENTITY_TYPE> {

    void delete(ENTITY_TYPE entity);
    ENTITY_TYPE update(ENTITY_TYPE entity);
    ENTITY_TYPE findByUsername(String username);
    boolean authenticate(String username, String password);
    void activate(ENTITY_TYPE entity);
    void changePassword(String username, String oldPassword, String newPassword);
}
