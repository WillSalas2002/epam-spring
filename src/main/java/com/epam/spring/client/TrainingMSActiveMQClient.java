package com.epam.spring.client;

import com.epam.spring.entity.TrainingRequest;
import com.epam.spring.service.auth.JwtService;
import com.epam.spring.util.TransactionContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.Queue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainingMSActiveMQClient implements CustomClient {

    private final Queue trainingQueue;
    private final JwtService jwtService;
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public void sendSavingOrDeletingRequest(TrainingRequest trainingRequest) {
        try {
            String jwtToken = jwtService.generateTokenForSecondMicroservice();

            // Convert TrainingRequest to JSON string
            String messageBody = objectMapper.writeValueAsString(trainingRequest);

            log.info("Transaction ID: {}, sending request to training-ms via ActiveMQ: {}",
                    TransactionContext.getTransactionId(), messageBody);

            // Send as TextMessage (String)
            jmsTemplate.convertAndSend(trainingQueue, messageBody, message -> {
                message.setStringProperty("Authorization", "Bearer " + jwtToken);
                return message;
            });

        } catch (JsonProcessingException e) {
            log.error("Error serializing TrainingRequest: {}", e.getMessage());
        }
    }
}
