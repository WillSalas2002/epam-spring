package com.epam.spring.util;

import com.epam.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsernameGenerator {

    public static final String DOT_SIGN = ".";
    private final UserRepository userRepository;

    public String generateUniqueUsername(String firstName, String lastName) {
        String baseUsername = firstName + DOT_SIGN + lastName;
        int serial = 1;
        while (userRepository.findByUsername(baseUsername).isPresent()) {
            baseUsername = baseUsername + DOT_SIGN + serial++;
        }
        return baseUsername;
    }
}
