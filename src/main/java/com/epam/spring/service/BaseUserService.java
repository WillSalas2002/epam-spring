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

    public UserCredentialsResponseDTO changeCredentials(CredentialChangeRequestDTO credentialChangeRequest) {
        User user = findUserOrThrowException(credentialChangeRequest.getUsername());
        if (!Objects.equals(user.getPassword(), credentialChangeRequest.getOldPassword())) {
            throw new RuntimeException("Incorrect old password");
        }
        user.setPassword(credentialChangeRequest.getNewPassword());
        userRepository.update(user);
        return new UserCredentialsResponseDTO(user.getUsername(), user.getPassword());
    }

    public boolean login(UserCredentialsRequestDTO userCredentialsRequest) {
        User user = findUserOrThrowException(userCredentialsRequest.getUsername());
        return Objects.equals(user.getPassword(), userCredentialsRequest.getPassword());
    }

    public void activateProfile(UserActivationRequestDTO activationRequest) {
        User user = findUserOrThrowException(activationRequest.getUsername());
        user.setActive(!user.isActive());
        userRepository.update(user);
    }

    private User findUserOrThrowException(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User with username " + username + " not found"));
    }
}
