package com.epam.spring.client;

import com.epam.spring.entity.TrainerMonthlySummary;
import com.epam.spring.entity.TrainingRequest;
import com.epam.spring.service.impl.TrainerService;
import com.epam.spring.util.TransactionContext;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainingMSClient {

    private final static String URL_SAVE_OR_DELETE = "http://training-ms/api/v1/trainings";
    public static final String URL_TEMPLATE_TRAINER_SUMMARY = "http://training-ms/api/v1/trainers/%s/summary";

    private final RestTemplate restTemplate;
    private final TrainerService trainerService;

    @CircuitBreaker(name = "training-ms", fallbackMethod = "fallbackForSavingOrDeleting")
    public void sendSavingOrDeletingRequest(TrainingRequest trainingRequest) {
        log.info("Transaction ID: {}, sending request to training-ms: {}", TransactionContext.getTransactionId(), trainingRequest);
        HttpEntity<TrainingRequest> request = new HttpEntity<>(trainingRequest);
        restTemplate.exchange(URL_SAVE_OR_DELETE, HttpMethod.POST, request, Void.class);
    }

    @CircuitBreaker(name = "training-ms", fallbackMethod = "fallbackForGettingSummary")
    public ResponseEntity<TrainerMonthlySummary> getTrainerMonthlySummary(@PathVariable("username") String username) {
        log.info("Transaction ID: {}, sending request to training-ms for getting summary for a trainer: {}", TransactionContext.getTransactionId(), username);
        trainerService.getUserProfile(username);
        return getSummaryForTrainer(username);
    }

    @CircuitBreaker(name = "training-ms", fallbackMethod = "fallbackForGettingSummary")
    private ResponseEntity<TrainerMonthlySummary> getSummaryForTrainer(String username) {
        String url = String.format(URL_TEMPLATE_TRAINER_SUMMARY, username);
        return ResponseEntity.ok(restTemplate.getForObject(url, TrainerMonthlySummary.class));
    }

    private ResponseEntity<TrainerMonthlySummary> fallbackForGettingSummary(String username, Exception ex) {
        log.error("Transaction ID: {}, fallback triggered when trying to get summary for trainer: {}, due to: {}", TransactionContext.getTransactionId(), username, ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new TrainerMonthlySummary());
    }

    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    private void fallbackForSavingOrDeleting(TrainingRequest trainingRequest, Throwable ex) {
        log.error("Transaction ID: {}, fallback triggered due to: {}", TransactionContext.getTransactionId(), ex.getMessage());
    }
}
