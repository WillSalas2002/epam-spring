package com.epam.spring.util;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UsernameGenerator {

    public static final String DOT_SIGN = ".";

    public String generateUniqueUsername(String firstName, String lastName, Set<String> usernames) {
        String baseUsername = firstName + DOT_SIGN + lastName;
        String uniqueUsername = baseUsername;
        int counter = 1;

        while (usernames.contains(uniqueUsername)) {
            uniqueUsername = baseUsername + DOT_SIGN + counter;
            counter++;
        }

        usernames.add(uniqueUsername);
        return uniqueUsername;
    }
}
