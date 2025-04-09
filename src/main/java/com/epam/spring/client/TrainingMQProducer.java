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
public class TrainingMQProducer {

    public static final String TRAINING_TYPE_ID_PROPERTY_NAME = "training";
    public static final String TRAINING_TYPE_ID_PROPERTY_VALUE = "com.epam.training.dto.TrainingRequest";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTH_HEADER_NAME = "Authorization";

    private final Queue trainingQueue;
    private final JwtService jwtService;
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessageToTrainingQueue(TrainingRequest trainingRequest) {
        try {
            String jwtToken = jwtService.generateTokenForTrainingMS();

            String messageBody = objectMapper.writeValueAsString(trainingRequest);

            log.info("Transaction ID: {}, sending message to training-ms via ActiveMQ: {}",
                    TransactionContext.getTransactionId(), messageBody);

            jmsTemplate.convertAndSend(trainingQueue, messageBody, message -> {
                message.setStringProperty(AUTH_HEADER_NAME, BEARER_PREFIX + jwtToken);
                message.setStringProperty(TRAINING_TYPE_ID_PROPERTY_NAME, TRAINING_TYPE_ID_PROPERTY_VALUE);
                return message;
            });

        } catch (JsonProcessingException e) {
            log.error("Error serializing TrainingRequest: {}", e.getMessage());
        }
    }
}
