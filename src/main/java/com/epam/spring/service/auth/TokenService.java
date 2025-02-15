package com.epam.spring.service.auth;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class TokenService {

    private final Set<String> BLACK_LIST_TOKENS = new HashSet<>();
    private final Map<String, String> USER_TOKENS = new HashMap<>();

    public void addTokenToBlackList(String token) {
        BLACK_LIST_TOKENS.add(token);
    }

    public boolean isTokenBlackListed(String token) {
        return BLACK_LIST_TOKENS.contains(token);
    }

    public void updateUserToken(String username, String newToken) {
        String oldToken = USER_TOKENS.get(username);
        if (oldToken != null) {
            addTokenToBlackList(oldToken);
        }
        USER_TOKENS.put(username, newToken);
    }
}
