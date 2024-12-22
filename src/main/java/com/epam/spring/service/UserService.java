package com.epam.spring.service;

import com.epam.spring.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    public static final String DOT_SIGN = ".";
    private final UserRepository userRepository;

    public String generateUniqueUsername(String firstName, String lastName) {
        String baseUsername = firstName + DOT_SIGN + lastName;
        int serial = 1;
        while (userRepository.existsByUsername(baseUsername)) {
            baseUsername += serial++;
        }
        return baseUsername;
    }
}
