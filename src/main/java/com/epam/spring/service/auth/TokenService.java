package com.epam.spring.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtService jwtService;
    private final Set<String> blackListTokens = new HashSet<>();
    private final Map<String, String> userTokens = new HashMap<>();

    public void addTokenToBlackList(String token) {
        if (blackListTokens.size() >= 50) {
            clearExpiredTokensFromBlackList();
        }
        blackListTokens.add(token);
    }

    private void clearExpiredTokensFromBlackList() {
        blackListTokens.removeIf(jwtService::isTokenExpired);
    }

    public boolean isTokenBlackListed(String token) {
        return blackListTokens.contains(token);
    }

    public void updateUserToken(String username, String newToken) {
        String oldToken = userTokens.get(username);
        if (oldToken != null) {
            addTokenToBlackList(oldToken);
        }
        userTokens.put(username, newToken);
    }
}
