package com.epam.spring.util;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UsernameGenerator {

    public String generateUniqueUsername(String baseUsername, Set<String> usernames) {
        String uniqueUsername = baseUsername;
        int counter = 1;

        while (usernames.contains(uniqueUsername)) {
            uniqueUsername = baseUsername + "." + counter;
            counter++;
        }

        usernames.add(uniqueUsername);
        return uniqueUsername;
    }
}
