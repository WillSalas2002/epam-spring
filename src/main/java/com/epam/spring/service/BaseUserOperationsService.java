package com.epam.spring.service;

public interface BaseUserOperationsService<CREATE_REQUEST, CREATE_RESPONSE, GET_RESPONSE, UPDATE_REQUEST, UPDATE_RESPONSE, ACTIVATE_REQUEST> {

    CREATE_RESPONSE create(CREATE_REQUEST createRequest);
    GET_RESPONSE getUserProfile(String username);
    UPDATE_RESPONSE updateProfile(String username, UPDATE_REQUEST updateRequest);
    void activateProfile(String username, ACTIVATE_REQUEST activateRequest);
}
