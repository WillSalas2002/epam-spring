package com.epam.spring.service.impl;

import com.epam.spring.dto.request.user.CredentialChangeRequestDTO;
import com.epam.spring.dto.request.user.UserCredentialsRequestDTO;
import com.epam.spring.dto.response.JwtAuthenticationResponse;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.error.exception.IncorrectCredentialsException;
import com.epam.spring.error.exception.LoginAttemptException;
import com.epam.spring.error.exception.ResourceNotFoundException;
import com.epam.spring.model.Token;
import com.epam.spring.model.User;
import com.epam.spring.repository.TokenRepository;
import com.epam.spring.repository.UserRepository;
import com.epam.spring.service.auth.JwtService;
import com.epam.spring.service.auth.LoginAttemptService;
import com.epam.spring.service.auth.MyUserPrincipal;
import com.epam.spring.util.TransactionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final LoginAttemptService loginAttemptService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public UserCredentialsResponseDTO changeCredentials(CredentialChangeRequestDTO credentialChangeRequest) {
        String username = credentialChangeRequest.getUsername();
        String transactionId = TransactionContext.getTransactionId();
        log.info("Transaction ID: {}, Changing credentials for user: {}",
                transactionId, username);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, credentialChangeRequest.getOldPassword()));
        MyUserPrincipal userPrincipal = (MyUserPrincipal) userDetailsService.loadUserByUsername(username);
        User user = userPrincipal.getUser();
        user.setPassword(passwordEncoder.encode(credentialChangeRequest.getNewPassword()));
        userRepository.save(user);
        log.info("Transaction ID: {}, Successfully changed credentials for user: {}",
                transactionId, username);
        return new UserCredentialsResponseDTO(user.getUsername(), user.getPassword());
    }

    public JwtAuthenticationResponse login(UserCredentialsRequestDTO request) {
        String username = request.getUsername();
        if (loginAttemptService.isBlocked(username)) {
            throw new LoginAttemptException();
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, request.getPassword()));
            MyUserPrincipal userPrincipal = (MyUserPrincipal) userDetailsService.loadUserByUsername(username);
            loginAttemptService.resetAttempts(username);
            String jwtToken = jwtService.generateToken(userPrincipal);
            revokeAllUserTokens(userPrincipal.getUser());
            saveToken(jwtToken, userPrincipal.getUser());
            return new JwtAuthenticationResponse(jwtToken);
        } catch (BadCredentialsException ex) {
            loginAttemptService.loginFailed(username);
            throw new IncorrectCredentialsException();
        }
    }

    private void saveToken(String token, User user) {
        tokenRepository.save(Token.builder()
                .token(token)
                .expired(false)
                .revoked(false)
                .user(user)
                .build());
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void activateProfile(String username) {
        User user = findUserOrThrowException(username);
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    private User findUserOrThrowException(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(ResourceNotFoundException::new);
    }
}
