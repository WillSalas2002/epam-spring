package com.epam.spring.client;

import com.epam.spring.entity.TrainingRequest;
import com.epam.spring.service.auth.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.Message;
import jakarta.jms.Queue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrainingMQProducerTest {

    private JmsTemplate jmsTemplate;
    private JwtService jwtService;
    private ObjectMapper objectMapper;
    private Queue trainingQueue;

    private TrainingMQProducer producer;

    @BeforeEach
    void setUp() {
        jmsTemplate = mock(JmsTemplate.class);
        jwtService = mock(JwtService.class);
        objectMapper = new ObjectMapper();
        trainingQueue = mock(Queue.class);

        producer = new TrainingMQProducer(trainingQueue, jwtService, jmsTemplate, objectMapper);
    }

    @Test
    void shouldSendSerializedMessageWithHeaders() throws Exception {
        // Given
        TrainingRequest trainingRequest = TrainingRequest.builder()
                .username("Adam Salas")
                .build();

        String expectedJwt = "mocked.jwt.token";
        when(jwtService.generateTokenForTrainingMS()).thenReturn(expectedJwt);

        // When
        producer.sendMessageToTrainingQueue(trainingRequest);

        // Then
        verify(jmsTemplate).convertAndSend(eq(trainingQueue), any(String.class), any());

        // Capture the message post-processor
        ArgumentCaptor<MessagePostProcessor> processorCaptor = ArgumentCaptor.forClass(MessagePostProcessor.class);
        verify(jmsTemplate).convertAndSend(eq(trainingQueue), eq(objectMapper.writeValueAsString(trainingRequest)), processorCaptor.capture());

        // Simulate message to test headers
        Message mockMessage = mock(Message.class);
        processorCaptor.getValue().postProcessMessage(mockMessage);

        verify(mockMessage).setStringProperty(TrainingMQProducer.AUTH_HEADER_NAME, TrainingMQProducer.BEARER_PREFIX + expectedJwt);
        verify(mockMessage).setStringProperty(TrainingMQProducer.TRAINING_TYPE_ID_PROPERTY_NAME, TrainingMQProducer.TRAINING_TYPE_ID_PROPERTY_VALUE);
    }
}