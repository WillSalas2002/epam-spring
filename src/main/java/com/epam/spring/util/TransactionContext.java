package com.epam.spring.util;

import java.util.UUID;

public class TransactionContext {
    private static final ThreadLocal<String> transactionId = new ThreadLocal<>();

    public static void setTransactionId(String id) {
        transactionId.set(id);
    }

    public static String getTransactionId() {
        return transactionId.get();
    }

    public static void clear() {
        transactionId.remove();
    }

    public static String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
}
