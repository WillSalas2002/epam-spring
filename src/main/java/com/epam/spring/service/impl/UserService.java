package com.epam.spring.service.impl;

import com.epam.spring.dto.request.user.CredentialChangeRequestDTO;
import com.epam.spring.dto.request.user.UserCredentialsRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.error.exception.IncorrectCredentialsException;
import com.epam.spring.error.exception.ResourceNotFoundException;
import com.epam.spring.model.User;
import com.epam.spring.repository.UserRepository;
import com.epam.spring.util.TransactionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserCredentialsResponseDTO changeCredentials(CredentialChangeRequestDTO credentialChangeRequest) {
        String username = credentialChangeRequest.getUsername();
        String transactionId = TransactionContext.getTransactionId();
        log.info("Transaction ID: {}, Changing credentials for user: {}",
                transactionId, username);
        User user = findUserOrThrowException(username);
        checkPassword(credentialChangeRequest.getOldPassword(), user);
        user.setPassword(credentialChangeRequest.getNewPassword());
        userRepository.save(user);
        log.info("Transaction ID: {}, Successfully changed credentials for user: {}",
                transactionId, username);
        return new UserCredentialsResponseDTO(user.getUsername(), user.getPassword());
    }

    @Transactional(readOnly = true)
    public void login(UserCredentialsRequestDTO userCredentialsRequest) {
        String username = userCredentialsRequest.getUsername();
        String transactionId = TransactionContext.getTransactionId();
        log.info("Transaction ID: {}, Logging in user: {}",
                transactionId, username);
        User user = findUserOrThrowException(userCredentialsRequest.getUsername());
        checkPassword(userCredentialsRequest.getPassword(), user);
    }

    public void activateProfile(String username) {
        User user = findUserOrThrowException(username);
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public void authenticate(String username, String password) {
        User user = findUserOrThrowException(username);
        checkPassword(password, user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsernameAndPassword(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }

    private static void checkPassword(String password, User user) {
        if (!Objects.equals(user.getPassword(), password)) {
            log.info("Transaction ID: {}, Incorrect password, user: {}",
                    TransactionContext.generateTransactionId(), password);
            throw new IncorrectCredentialsException("Incorrect password");
        }
    }

    private User findUserOrThrowException(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(ResourceNotFoundException::new);
    }
}
