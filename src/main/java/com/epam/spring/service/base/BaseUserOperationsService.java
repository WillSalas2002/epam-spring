package com.epam.spring.service.base;

public interface BaseUserOperationsService<CREATE_REQUEST, CREATE_RESPONSE, GET_RESPONSE, UPDATE_REQUEST, UPDATE_RESPONSE> {

    CREATE_RESPONSE create(CREATE_REQUEST createRequest);
    GET_RESPONSE getUserProfile(String username);
    UPDATE_RESPONSE updateProfile(UPDATE_REQUEST updateRequest);
}
