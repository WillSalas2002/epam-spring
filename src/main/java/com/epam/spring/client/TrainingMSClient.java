package com.epam.spring.client;

import com.epam.spring.entity.TrainingRequest;
import com.epam.spring.util.TransactionContext;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainingMSClient {

    @Value("${training-ms.url}")
    private String trainingMSURL;

    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "training-ms", fallbackMethod = "fallbackForSavingOrDeleting")
    public void sendSavingOrDeletingRequest(TrainingRequest trainingRequest) {
        log.info("Transaction ID: {}, sending request to training-ms: {}", TransactionContext.getTransactionId(), trainingRequest);
        HttpEntity<TrainingRequest> request = new HttpEntity<>(trainingRequest);
        restTemplate.exchange(trainingMSURL, HttpMethod.POST, request, Void.class);
    }

    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    private void fallbackForSavingOrDeleting(TrainingRequest trainingRequest, Throwable ex) {
        log.error("Transaction ID: {}, fallback triggered due to: {}", TransactionContext.getTransactionId(), ex.getMessage());
    }
}
