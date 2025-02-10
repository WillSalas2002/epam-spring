package com.epam.spring.logging;

import com.epam.spring.util.TransactionContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String transactionId = TransactionContext.generateTransactionId();
        TransactionContext.setTransactionId(transactionId);

        log.info("Transaction ID: {}, Incoming Request: {} {}",
                transactionId, request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.info("Transaction ID: {}, Response Status: {}",
                TransactionContext.getTransactionId(), response.getStatus());
        TransactionContext.clear();
    }
}
