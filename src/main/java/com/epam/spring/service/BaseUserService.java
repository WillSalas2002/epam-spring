package com.epam.spring.service;

import com.epam.spring.dto.request.user.CredentialChangeRequestDTO;
import com.epam.spring.dto.request.user.UserActivationRequestDTO;
import com.epam.spring.dto.request.user.UserCredentialsRequestDTO;
import com.epam.spring.dto.response.UserCredentialsResponseDTO;
import com.epam.spring.model.User;
import com.epam.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.NoSuchElementException;
import java.util.Objects;

@RequiredArgsConstructor
public abstract class BaseUserService {

    private final UserRepository userRepository;

    public UserCredentialsResponseDTO changeCredentials(String username, CredentialChangeRequestDTO credentialChangeRequest) {
        User user = findUserOrThrowException(username);
        if (!Objects.equals(user.getPassword(), credentialChangeRequest.getOldPassword())) {
            throw new RuntimeException("Incorrect old password");
        }
        user.setPassword(credentialChangeRequest.getNewPassword());
        userRepository.update(user);
        return new UserCredentialsResponseDTO(user.getUsername(), user.getPassword());
    }

    public boolean login(String username, UserCredentialsRequestDTO userCredentialsRequest) {
        User user = findUserOrThrowException(username);
        return Objects.equals(user.getPassword(), userCredentialsRequest.getPassword());
    }

    public void activateProfile(String username, UserActivationRequestDTO activationRequest) {
        User user = findUserOrThrowException(username);
        user.setActive(!user.isActive());
        userRepository.update(user);
    }

    private User findUserOrThrowException(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User with username " + username + " not found"));
    }
}
