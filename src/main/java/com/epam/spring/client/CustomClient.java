package com.epam.spring.client;

import com.epam.spring.entity.TrainingRequest;

public interface CustomClient {

    void sendSavingOrDeletingRequest(TrainingRequest trainingRequest);
}
